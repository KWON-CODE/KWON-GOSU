package com.cleaning.platform.controller;

import com.cleaning.platform.domain.ProviderType;
import com.cleaning.platform.dto.PageResponseDto;
import com.cleaning.platform.dto.ProviderListDto;
import com.cleaning.platform.service.NtsApiService;
import com.cleaning.platform.service.ProviderQueryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProviderApiController {

    private final NtsApiService ntsApiService;
    private final ProviderQueryService providerQueryService;
    private final ObjectMapper objectMapper;

    @GetMapping("/api/providers/check-brn")
    public ResponseEntity<Map<String, Boolean>> checkBusinessRegistrationNumber(@RequestParam("brn") String brn) {
        boolean isValid = ntsApiService.validateBusinessRegistrationNumber(brn);
        return ResponseEntity.ok(Collections.singletonMap("isValid", isValid));
    }

    @GetMapping(value = "/api/providers", produces = "application/json; charset=utf-8")
    public String getProviders(
            @RequestParam(required = false) ProviderType type,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ProviderListDto> pageData = providerQueryService.searchProviders(type, keyword, pageable);
        PageResponseDto<ProviderListDto> responseDto = new PageResponseDto<>(pageData);

        try {
            return objectMapper.writeValueAsString(responseDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{\"error\": \"JSON 변환 중 오류가 발생했습니다.\"}";
        }
    }
}