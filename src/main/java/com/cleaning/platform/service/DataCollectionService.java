package com.cleaning.platform.service;

import com.cleaning.platform.domain.ProviderType;
import com.cleaning.platform.domain.ServiceProvider;
import com.cleaning.platform.repository.ServiceProviderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DataCollectionService {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${api.gg-data.moving-company-key}")
    private String movingCompanyKey;

    @Value("${api.naver.client-id}")
    private String naverClientId;

    @Value("${api.naver.client-secret}")
    private String naverClientSecret;

    private final WebClient webClient = WebClient.builder()
            .exchangeStrategies(ExchangeStrategies.builder()
                    .codecs(configurer -> configurer
                            .defaultCodecs()
                            .maxInMemorySize(16 * 1024 * 1024))
                    .build())
            .build();

    public void fetchMovingCompanyData() {
        log.info("경기도 이사업체 데이터 수집을 시작합니다.");
        String apiName = "GGCSMFSTT";

        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.gg.go.kr")
                .path("/" + apiName)
                .queryParam("KEY", movingCompanyKey)
                .queryParam("Type", "json")
                .queryParam("pIndex", 1)
                .queryParam("pSize", 1000)
                .encode()
                .build()
                .toUri();

        try {
            String responseBody = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (responseBody != null) {
                parseAndSaveMovingData(responseBody, apiName, ProviderType.MOVING);
            }
        } catch (Exception e) {
            log.error("API 호출 중 심각한 오류 발생", e);
        }
    }

    private void parseAndSaveMovingData(String responseBody, String apiName, ProviderType providerType) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode ggcsmfsttArray = rootNode.path(apiName);
            if (!ggcsmfsttArray.isArray() || ggcsmfsttArray.size() < 2) {
                log.warn("[{}] API 응답 구조가 예상과 다릅니다. 전체 응답: {}", apiName, responseBody);
                return;
            }
            JsonNode rows = ggcsmfsttArray.get(1).path("row");

            if (rows.isMissingNode() || !rows.isArray()) {
                log.warn("[{}] API 응답에서 유효한 데이터('row')를 찾지 못했습니다. 전체 응답: {}", apiName, responseBody);
                return;
            }

            for (JsonNode item : rows) {
                String bsnStateDiv = item.path("BSN_STATE_DIV").asText();
                String providerName = item.path("ENTRPS_NM").asText();
                String contactPhone = item.path("TELNO").asText("정보 없음");

                if (bsnStateDiv != null && (bsnStateDiv.contains("영업") || bsnStateDiv.contains("정상"))) {
                    if (providerName == null || providerName.isBlank()) continue;

                    Optional<ServiceProvider> existingProvider =
                            serviceProviderRepository.findByProviderName(providerName);

                    if (existingProvider.isEmpty()) {
                        String tempBizRegNo = "CRAWLED-" + providerName + "-" + UUID.randomUUID().toString().substring(0, 4);

                        ServiceProvider newProvider = ServiceProvider.builder()
                                .id("P-" + UUID.randomUUID().toString().substring(0, 7))
                                .providerName(providerName)
                                .businessRegistrationNumber(tempBizRegNo)
                                .contactPhone(contactPhone)
                                .contactEmail(providerName.replaceAll("\\s+", "") + "@crawled.com")
                                .providerType(providerType)
                                .build();

                        newProvider.setDataSource(ServiceProvider.DataSource.CRAWLED);
                        serviceProviderRepository.save(newProvider);
                        log.info("[{}] 신규 업체 저장: {}", apiName, providerName);
                    }
                }
            }
        } catch (JsonProcessingException e) {
            log.error("[{}] 데이터 JSON 파싱 중 오류 발생", apiName, e);
        }
    }


    public void enrichDataWithNaverApi() {
        log.info("네이버 API를 이용한 업체 정보 보강 작업을 시작합니다.");

        List<ServiceProvider> targetProviders =
                serviceProviderRepository.findByDataSourceAndProviderTypeAndExternalPlaceUrlIsNull(
                        ServiceProvider.DataSource.CRAWLED, ProviderType.MOVING);

        if (targetProviders.isEmpty()) {
            log.info("정보를 보강할 신규 업체가 없습니다. 작업을 종료합니다.");
            return;
        }

        log.info("총 {}개의 업체에 대한 정보 보강을 시도합니다.", targetProviders.size());

        for (ServiceProvider provider : targetProviders) {
            String query = provider.getProviderName();

            String cleanQuery = query
                    .replaceAll("\\(.*?\\)", "") // 괄호와 내용 제거
                    .replaceAll("\\(주\\)|주식회사", "") // 법인명 제거
                    .trim();

            try {
                Thread.sleep(100); // 0.1초 대기

                // [핵심 수정] UriComponentsBuilder로 URL을 먼저 정밀하게 만듭니다.
                URI uri = UriComponentsBuilder
                        .fromUriString("https://openapi.naver.com/v1/search/local.json")
                        .queryParam("query", cleanQuery)
                        .queryParam("display", 1)
                        .encode() // 한글/특수문자 처리
                        .build()
                        .toUri();

                String responseBody = webClient.get()
                        .uri(uri) // 만들어진 URI 객체 사용
                        .header("X-Naver-Client-Id", naverClientId)
                        .header("X-Naver-Client-Secret", naverClientSecret)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                if (responseBody != null) {
                    parseAndSaveNaverData(responseBody, provider.getProviderType());
                }

            } catch (Exception e) {
                log.error("'{}' 업체 정보 보강 중 오류 발생: {}", query, e.getMessage());
            }
        }
    }


    public void fetchAirconSellersFromNaver() {
        log.info("네이버 API를 이용한 서울/경기 에어컨 업체 수집을 시작합니다.");


        List<String> locations = Arrays.asList(
                // 서울특별시
                "강남구"

        );
        List<String> keywords = Arrays.asList("에어컨 설치", "에어컨 청소");


        for (String location : locations) {
            for (String keyword : keywords) {
                String query = location + " " + keyword;
                log.info("==> '{}' 키워드로 네이버 지역 검색을 시작합니다.", query);


                for (int start = 1; start <= 1000; start += 5) {
                    final int currentStart = start;
                    try {

                        Thread.sleep(500); //

                        URI uri = UriComponentsBuilder
                                .fromUriString("https://openapi.naver.com/v1/search/local.json")
                                .queryParam("query", query)
                                .queryParam("display", 5)
                                .queryParam("start", currentStart)
                                .encode()
                                .build()
                                .toUri();

                        String responseBody = webClient.get()
                                .uri(uri)
                                .header("X-Naver-Client-Id", naverClientId)
                                .header("X-Naver-Client-Secret", naverClientSecret)
                                .retrieve()
                                .bodyToMono(String.class)
                                .block();

                        if (responseBody != null) {
                            boolean hasMore = parseAndSaveNaverData(responseBody, ProviderType.AC);
                            if (!hasMore) {
                                log.info("'{}' 키워드에 대한 결과가 더 이상 없습니다. 다음 검색으로 넘어갑니다.", query);
                                break;
                            }
                        }

                    } catch (Exception e) {
                        log.error("'{}' 검색 중 오류 발생: {}", query, e.getMessage());
                        break;
                    }
                }
            }
        }
        log.info("서울/경기 에어컨 업체 수집 작업을 완료했습니다.");
    }


    private boolean parseAndSaveNaverData(String responseBody, ProviderType providerType) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode items = rootNode.path("items");

        if (!items.isArray() || items.isEmpty()) {
            return false;
        }

        for (JsonNode item : items) {
            String rawTitle = item.path("title").asText();
            String providerName = rawTitle.replaceAll("<(/)?b>", "");
            String contactPhone = item.path("telephone").asText("정보 없음");

            if (providerName.isBlank()) continue;

            Optional<ServiceProvider> existingProviderOpt =
                    serviceProviderRepository.findByProviderName(providerName);


            if (existingProviderOpt.isEmpty()) {

                String finalBizRegNo = UUID.randomUUID().toString();

                ServiceProvider newProvider = ServiceProvider.builder()
                        .id("P-" + UUID.randomUUID().toString().substring(0, 7))
                        .providerName(providerName)
                        .businessRegistrationNumber(finalBizRegNo)
                        .contactPhone(contactPhone)
                        .contactEmail(providerName.replaceAll("\\s+", "") + "@crawled.com")
                        .providerType(providerType)
                        .build();

                newProvider.setDataSource(ServiceProvider.DataSource.CRAWLED);
                newProvider.setExternalPlaceUrl(item.path("link").asText(null));
                newProvider.setNaverBlogReviewCount(item.path("blogger_review_count").asInt(0));
                newProvider.setNaverVisitorReviewCount(item.path("visitor_review_count").asInt(0));

                serviceProviderRepository.save(newProvider);
                log.info("[네이버 수집] 신규 {} 업체 저장: {} (방문자 리뷰: {})", providerType, providerName, newProvider.getNaverVisitorReviewCount());

            } else {

                ServiceProvider existingProvider = existingProviderOpt.get();

                int visitorReviewCount = item.path("visitor_review_count").asInt(0);

                if (existingProvider.getNaverVisitorReviewCount() == null || !existingProvider.getNaverVisitorReviewCount().equals(visitorReviewCount)) {
                    existingProvider.setExternalPlaceUrl(item.path("link").asText(null));
                    existingProvider.setNaverBlogReviewCount(item.path("blogger_review_count").asInt(0));
                    existingProvider.setNaverVisitorReviewCount(visitorReviewCount);

                    serviceProviderRepository.save(existingProvider);
                    log.info("[네이버 업데이트] 기존 {} 업체 정보 업데이트: {} (방문자 리뷰: {})", providerType, providerName, visitorReviewCount);
                }
            }
        }
        return true;
    }
}