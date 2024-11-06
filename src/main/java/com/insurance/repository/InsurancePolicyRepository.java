package com.insurance.repository;

import com.insurance.entity.InsurancePolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicy, Long> {
    Page<InsurancePolicy> findByUserId(Long id, Pageable pageable);
}
