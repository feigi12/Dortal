package com.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.insurance.repository.UserRepository;
import com.insurance.security.JwtTokenProvider;
import com.insurance.entity.InsurancePolicy;
import com.insurance.entity.User;
import com.insurance.repository.InsurancePolicyRepository;
import com.insurance.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

@TestPropertySource(properties ={"jwt.secret=MySecretKey", "jwt.expiration=3600"})
@SpringBootTest
@AutoConfigureMockMvc
public class InsurancePolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InsurancePolicyRepository policyRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private UserRepository userRepository;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtToken = "mockedJwtToken";

        when(jwtTokenProvider.renewTokenIfNeeded(anyString())).thenReturn(jwtToken);
    }

    @Test
    @WithMockUser
    public void testCreatePolicy() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        InsurancePolicy policy = new InsurancePolicy();
        policy.setPolicyHolderName("New Holder");

        when(userService.loadUserByUsername("testUser")).thenReturn(user);
        when(policyRepository.save(any(InsurancePolicy.class))).thenReturn(policy);

        mockMvc.perform(post("/api/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"policyHolderName\": \"New Holder\"}")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.policyHolderName").value("New Holder"));
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testUpdatePolicy() throws Exception {
        InsurancePolicy existingPolicy = new InsurancePolicy();
        existingPolicy.setId(1L);
        existingPolicy.setUser(new User());

        when(policyRepository.findById(1L)).thenReturn(Optional.of(existingPolicy));

        mockMvc.perform(delete("/api/policies/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        verify(policyRepository).findById(1L);
        verify(policyRepository).delete(existingPolicy);
    }

    @Test
    @WithMockUser
    public void testDeletePolicy() throws Exception {
        InsurancePolicy existingPolicy = new InsurancePolicy();
        existingPolicy.setId(1L);
        existingPolicy.setUser(new User());

        when(policyRepository.findById(1L)).thenReturn(Optional.of(existingPolicy));

        mockMvc.perform(delete("/api/policies/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        verify(policyRepository).findById(1L);
        verify(policyRepository).delete(existingPolicy);
    }
}