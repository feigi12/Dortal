package com.example.Dortal.insurance.controller;

import com.example.Dortal.insurance.entity.InsurancePolicy;
import com.example.Dortal.insurance.entity.User;
import com.example.Dortal.insurance.repository.InsurancePolicyRepository;
import com.example.Dortal.insurance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/policies")
public class InsurancePolicyController {

    @Autowired
    private InsurancePolicyRepository policyRepository;
    private final UserService userService;

    public InsurancePolicyController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Page<InsurancePolicy> getUserPolicies(
            @AuthenticationPrincipal User userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {

        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortBy = Sort.by(direction, sort[0]);
        Pageable pageable = PageRequest.of(page, size, sortBy);

        return policyRepository.findByUserId(userDetails.getId(), pageable);
    }

    @PostMapping
    public InsurancePolicy createPolicy(@RequestBody InsurancePolicy policy,
                                        @AuthenticationPrincipal User userDetails) {
        User user = (User) userService.loadUserByUsername(userDetails.getUsername());
        policy.setUser(user);
        return policyRepository.save(policy);
    }
    @PutMapping("/{id}")
    public InsurancePolicy updatePolicy(@PathVariable Long id, @RequestBody InsurancePolicy policyDetails) {
        InsurancePolicy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        policy.setPolicyHolderName(policyDetails.getPolicyHolderName());
        policy.setPolicyType(policyDetails.getPolicyType());
        policy.setPremiumAmount(policyDetails.getPremiumAmount());
        policy.setStartDate(policyDetails.getStartDate());
        policy.setEndDate(policyDetails.getEndDate());

        return policyRepository.save(policy);
    }

    @DeleteMapping("/{id}")
    public void deletePolicy(@PathVariable Long id) {
        InsurancePolicy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        User user = policy.getUser();
        user.getPolicies().remove(policy);
        userService.saveUser(user);
    }
}
