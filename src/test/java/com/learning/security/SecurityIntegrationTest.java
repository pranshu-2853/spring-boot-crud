package com.learning.security;

import com.learning.dto.SoftwareEngineerResponseDto;
import com.learning.service.SoftwareEngineerService;
import com.learning.security.dto.AuthResponse;
import com.learning.security.dto.LoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("🔐 Security Integration Tests")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @MockBean
    private SoftwareEngineerService softwareEngineerService;

    @MockBean
    private AuthService authService;

    private final String BASE_URL = "/api/v1/software-engineer";

    // =====================================================
    // PUBLIC ENDPOINT TESTS
    // =====================================================

    @Nested
    @DisplayName("Public Endpoint Tests (/auth/**)")
    class PublicEndpointTests {

        @Test
        @DisplayName("Login should be accessible without token")
        void loginShouldBeAccessible() throws Exception {

            when(authService.login(any(LoginRequest.class)))
                    .thenReturn(new AuthResponse("dummy-token"));

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                {
                                  "username": "user",
                                  "password": "1234"
                                }
                                """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("dummy-token"));
        }

        @Test
        @DisplayName("Register should be accessible without token")
        void registerShouldBeAccessible() throws Exception {

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                {
                                  "username": "user",
                                  "password": "1234",
                                  "role": "ROLE_USER"
                                }
                                """))
                    .andExpect(status().isOk());
        }
    }

    // =====================================================
    // UNAUTHENTICATED ACCESS TESTS
    // =====================================================

    @Nested
    @DisplayName("Unauthenticated Access Tests")
    class UnauthenticatedAccessTests {

        @Test
        @DisplayName("GET without token → 401")
        void getWithoutTokenShouldReturn401() throws Exception {

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error").value("Unauthorized"));
        }

        @Test
        @DisplayName("POST without token → 401")
        void postWithoutTokenShouldReturn401() throws Exception {

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                {
                                  "name": "Test",
                                  "techStack": "Java"
                                }
                                """))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error").value("Unauthorized"));
        }
    }

    // =====================================================
    // INVALID TOKEN TEST
    // =====================================================

    @Nested
    @DisplayName("Invalid Token Tests")
    class InvalidTokenTests {

        @Test
        @DisplayName("Invalid token → 401")
        void invalidTokenShouldReturn401() throws Exception {

            when(jwtService.extractUsername("invalid-token"))
                    .thenReturn("user");

            when(jwtService.isTokenValid("invalid-token"))
                    .thenReturn(false);

            mockMvc.perform(get(BASE_URL)
                            .header("Authorization", "Bearer invalid-token"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error").value("Unauthorized"));
        }
    }

    // =====================================================
    // ROLE_USER TESTS
    // =====================================================

    @Nested
    @DisplayName("ROLE_USER Authorization Tests")
    class UserRoleTests {

        private void mockUserAuthentication() {
            UserDetails userDetails = new User(
                    "user",
                    "password",
                    List.of(() -> "ROLE_USER")
            );

            when(jwtService.extractUsername("user-token"))
                    .thenReturn("user");

            when(jwtService.isTokenValid("user-token"))
                    .thenReturn(true);

            when(userDetailsService.loadUserByUsername("user"))
                    .thenReturn(userDetails);
        }

        @Test
        @DisplayName("USER → GET → 200")
        void userCanAccessGet() throws Exception {

            mockUserAuthentication();

            when(softwareEngineerService.getSoftwareEngineers(any(), any()))
                    .thenReturn(Page.empty());

            mockMvc.perform(get(BASE_URL)
                            .header("Authorization", "Bearer user-token"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("USER → POST → 403")
        void userCannotPost() throws Exception {

            mockUserAuthentication();

            mockMvc.perform(post(BASE_URL)
                            .header("Authorization", "Bearer user-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                {
                                  "name": "Test",
                                  "techStack": "Java"
                                }
                                """))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error").value("Forbidden"));
        }
    }

    // =====================================================
    // ROLE_ADMIN TESTS
    // =====================================================

    @Nested
    @DisplayName("ROLE_ADMIN Authorization Tests")
    class AdminRoleTests {

        private void mockAdminAuthentication() {
            UserDetails adminDetails = new User(
                    "admin",
                    "password",
                    List.of(() -> "ROLE_ADMIN")
            );

            when(jwtService.extractUsername("admin-token"))
                    .thenReturn("admin");

            when(jwtService.isTokenValid("admin-token"))
                    .thenReturn(true);

            when(userDetailsService.loadUserByUsername("admin"))
                    .thenReturn(adminDetails);
        }

        @Test
        @DisplayName("ADMIN → POST → 201")
        void adminCanPost() throws Exception {

            mockAdminAuthentication();

            when(softwareEngineerService.insertSoftwareEngineer(any()))
                    .thenReturn(new SoftwareEngineerResponseDto(1, "Admin", "Java"));

            mockMvc.perform(post(BASE_URL)
                            .header("Authorization", "Bearer admin-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                {
                                  "name": "Admin",
                                  "techStack": "Java"
                                }
                                """))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("ADMIN → POST with invalid body → 400")
        void adminPostInvalidBodyShouldReturn400() throws Exception {

            mockAdminAuthentication();

            mockMvc.perform(post(BASE_URL)
                            .header("Authorization", "Bearer admin-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                {
                                  "name": "",
                                  "techStack": ""
                                }
                                """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.name").exists())
                    .andExpect(jsonPath("$.techStack").exists());
        }
    }
}