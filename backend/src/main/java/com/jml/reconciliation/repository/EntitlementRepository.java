package com.jml.reconciliation.repository;

import com.jml.reconciliation.entity.Entitlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntitlementRepository extends JpaRepository<Entitlement, Long> {
    List<Entitlement> findByUsername(String username);
    Optional<Entitlement> findByUsernameAndAppName(String username, String appName);
    void deleteByUsernameAndAppName(String username, String appName);
}
