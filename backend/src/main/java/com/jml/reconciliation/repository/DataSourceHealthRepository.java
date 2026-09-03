package com.jml.reconciliation.repository;

import com.jml.reconciliation.entity.DataSourceHealth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataSourceHealthRepository extends JpaRepository<DataSourceHealth, String> {
}
