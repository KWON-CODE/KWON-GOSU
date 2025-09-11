package com.cleaning.platform.controller;

import com.cleaning.platform.service.ProviderQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class TempAdminController {

    private final ProviderQueryService providerQueryService;

    @GetMapping("/provider/recalculate-scores")
    public String recalculate() {
        providerQueryService.recalculateAndSaveAllTrustScores();
        return "모든 업체의 신뢰도 점수 재계산 및 저장이 완료되었습니다. 이제 정렬이 정상적으로 동작합니다.";
    }
}