package com.jml.reconciliation.repository;

import com.jml.reconciliation.entity.DirectoryGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectoryGroupRepository extends JpaRepository<DirectoryGroup, Long> {
    List<DirectoryGroup> findByUsername(String username);
    void deleteByUsername(String username);
}
