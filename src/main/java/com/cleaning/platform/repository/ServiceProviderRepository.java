package com.cleaning.platform.repository;

import com.cleaning.platform.domain.ProviderType;
import com.cleaning.platform.domain.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, String>
, JpaSpecificationExecutor<ServiceProvider> {

    Optional<ServiceProvider> findByUsersEmail(String email);
    Optional<ServiceProvider> findByUsersId(String userId);


    List<ServiceProvider> findByProviderNameContaining(String keyword);
    @Query("SELECT sp FROM ServiceProvider sp LEFT JOIN FETCH sp.acServices LEFT JOIN FETCH sp.movingServices WHERE sp.id = :id")
    Optional<ServiceProvider> findWithServicesById(@Param("id") String providerId);

    Optional<ServiceProvider>
    findByBusinessRegistrationNumber(String businessRegistrationNumber);

    Optional<ServiceProvider> findByProviderName(String providerName);

    List<ServiceProvider> findByDataSourceAndExternalPlaceUrlIsNull(ServiceProvider.DataSource dataSource);

    List<ServiceProvider> findByDataSourceAndProviderTypeAndExternalPlaceUrlIsNull(
            ServiceProvider.DataSource dataSource,
            ProviderType providerType
    );


    @Query("SELECT sp FROM ServiceProvider sp LEFT JOIN FETCH sp.users")
    List<ServiceProvider> findAllWithUsers();
}