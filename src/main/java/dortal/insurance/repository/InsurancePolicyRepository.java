package dortal.insurance.repository;

import dortal.insurance.entity.InsurancePolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicy, Long> {
    Page<InsurancePolicy> findByUserId(Long id, Pageable pageable);
    Page<InsurancePolicy> findByUserIdAndPolicyType(Long id, String policyType, Pageable pageable);

}
