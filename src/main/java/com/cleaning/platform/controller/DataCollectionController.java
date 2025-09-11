package com.cleaning.platform.controller;

import com.cleaning.platform.service.DataCollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DataCollectionController {

    private final DataCollectionService dataCollectionService;

    @GetMapping("/api/collect/moving-company")
    public String collectMovingCompanyData() {
        dataCollectionService.fetchMovingCompanyData();
        return "경기도 이사업체 데이터 수집 작업이 시작되었습니다.로그를 확인하세요.";
    }

    @GetMapping("/api/collect/enrich-naver")
    public String enrichNaverData() {
        dataCollectionService.enrichDataWithNaverApi();
        return "네이버 API 정보 보강 작업이 시작되었습니다.로그를 확인하세요.";
    }


    @GetMapping("/api/collect/aircon-sellers")
    public String collectAirconSellers() {
        dataCollectionService.fetchAirconSellersFromNaver();
        return "서울/경기 에어컨 업체 데이터 수집 작업이 시작되었습니다";
    }
}