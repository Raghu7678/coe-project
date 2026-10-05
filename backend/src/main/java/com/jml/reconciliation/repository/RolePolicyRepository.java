package com.jml.reconciliation.repository;

import com.jml.reconciliation.entity.RolePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePolicyRepository extends JpaRepository<RolePolicy, Long> {
    List<RolePolicy> findByRoleName(String roleName);
}
