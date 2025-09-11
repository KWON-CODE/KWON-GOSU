package com.cleaning.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class NtsApiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Value("${nts.api.service-key}")
    private String serviceKey;

    private final String validationUrl =
            "https://api.odcloud.kr/api/nts-businessman/v1/status";


    public boolean validateBusinessRegistrationNumber(String businessNumber) {

        URI uri = UriComponentsBuilder
                .fromUriString(validationUrl)
                .queryParam("serviceKey", serviceKey)
                .build(true)
                .toUri();

        try {

            Map<String, String[]> requestData = Collections.singletonMap("b_no", new String[]{businessNumber});

            String jsonBody = objectMapper.writeValueAsString(requestData);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);


            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);


            Map<String, Object> response = restTemplate.postForObject(uri, requestEntity, Map.class);

            if (response != null && response.containsKey("data")) {
                Map<String, String> data = ((java.util.List<Map<String, String>>) response.get("data")).get(0);
                return "01".equals(data.get("b_stt_cd"));
            }
        } catch (Exception e) {
            System.out.println("!!! 국세청 API 호출 중 에러 발생 !!!");
            e.printStackTrace();
            return false;
        }
        return false;
    }
}