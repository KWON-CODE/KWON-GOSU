package com.cleaning.platform.service;

import com.cleaning.platform.domain.ProviderType;
import com.cleaning.platform.domain.ServiceProvider;
import com.cleaning.platform.dto.ProviderListDto;
import com.cleaning.platform.repository.ServiceProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProviderQueryService {

    private final ServiceProviderRepository serviceProviderRepository;

    public Page<ProviderListDto> searchProviders(ProviderType providerType, String keyword, Pageable pageable) {
        Specification<ServiceProvider> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (providerType != null) {
                predicates.add(criteriaBuilder.equal(root.get("providerType"), providerType));
            }
            if (keyword != null && !keyword.isBlank()) {
                predicates.add(criteriaBuilder.like(root.get("providerName"), "%" + keyword + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };


        Page<ServiceProvider> entityPage = serviceProviderRepository.findAll(spec, pageable);


        return entityPage.map(ProviderListDto::new);
    }

    @Transactional
    public void recalculateAndSaveAllTrustScores() {

        List<ServiceProvider> allProviders = serviceProviderRepository.findAll();

        for (ServiceProvider provider : allProviders) {
            provider.calculateAndSetTrustScore();
        }

        serviceProviderRepository.saveAll(allProviders);

        System.out.println("========================================================");
        System.out.println(allProviders.size() + "개의 업체의 신뢰도 점수 재계산을 완료하고 저장했습니다.");
        System.out.println("========================================================");
    }
}