package dortal.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dortal.insurance.repository.UserRepository;
import dortal.insurance.security.JwtTokenProvider;
import dortal.insurance.entity.InsurancePolicy;
import dortal.insurance.entity.User;
import dortal.insurance.repository.InsurancePolicyRepository;
import dortal.insurance.service.UserService;
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
        when(jwtTokenProvider.validateToken(jwtToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(jwtToken)).thenReturn("testUser");
    }
    @Test
    @WithMockUser
    public void testCreatePolicy() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        InsurancePolicy policy = new InsurancePolicy();
        policy.setPolicyHolderName("New Holder");
        policy.setUser(user);

        when(userService.loadUserByUsername("testUser")).thenReturn(user);
        when(policyRepository.save(any(InsurancePolicy.class))).thenReturn(policy);

        mockMvc.perform(post("/api/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"policyHolderName\": \"New Holder\"}")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.policyHolderName").value("New Holder"))
                .andExpect(jsonPath("$.user.id").value(1L));
    }
}