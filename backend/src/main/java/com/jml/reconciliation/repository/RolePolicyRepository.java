package com.jml.reconciliation.repository;

import com.jml.reconciliation.entity.RolePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RolePolicyRepository extends JpaRepository<RolePolicy, Long> {
    List<RolePolicy> findByRoleName(String roleName);
    Optional<RolePolicy> findByRoleNameAndApplicationName(String roleName, String applicationName);
}
