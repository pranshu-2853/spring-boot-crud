package com.learning.security;

import com.learning.security.dto.AuthResponse;
import com.learning.security.dto.LoginRequest;
import com.learning.security.dto.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    // =====================================================
    // REGISTER TESTS
    // =====================================================

    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {

        @Test
        @DisplayName("Should throw exception if username already exists")
        void shouldThrowException_whenUsernameExists() {

            RegisterRequest request = new RegisterRequest();
            request.setUsername("user");
            request.setPassword("1234");
            request.setRole(Role.ROLE_USER);

            when(userRepository.findByUsername("user"))
                    .thenReturn(Optional.of(new User()));

            assertThrows(RuntimeException.class, () ->
                    authService.register(request)
            );

            verify(userRepository).findByUsername("user");
            verify(userRepository, never()).save(any());
            verify(passwordEncoder, never()).encode(any());
        }

        @Test
        @DisplayName("Should encode password and save user when username is new")
        void shouldSaveUser_whenUsernameNotExists() {

            RegisterRequest request = new RegisterRequest();
            request.setUsername("user");
            request.setPassword("1234");
            request.setRole(Role.ROLE_USER);


            when(userRepository.findByUsername("user"))
                    .thenReturn(Optional.empty());

            when(passwordEncoder.encode("1234"))
                    .thenReturn("encoded-password");

            authService.register(request);

            ArgumentCaptor<User> captor =
                    ArgumentCaptor.forClass(User.class);

            verify(userRepository).save(captor.capture());

            User savedUser = captor.getValue();

            assertEquals("user", savedUser.getUsername());
            assertEquals("encoded-password", savedUser.getPassword());
            assertEquals(Role.ROLE_USER, savedUser.getRole());

            verify(passwordEncoder).encode("1234");
        }
    }

    // =====================================================
    // LOGIN TESTS
    // =====================================================

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should authenticate and generate token")
        void shouldAuthenticateAndGenerateToken() {

            LoginRequest request = new LoginRequest();
            request.setUsername("user");
            request.setPassword("1234");

            Authentication authentication = mock(Authentication.class);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);

            when(jwtService.generateToken(authentication))
                    .thenReturn("dummy-token");

            AuthResponse response = authService.login(request);

            assertNotNull(response);
            assertEquals("dummy-token", response.getToken());

            verify(authenticationManager).authenticate(any());
            verify(jwtService).generateToken(authentication);
        }

        @Test
        @DisplayName("Should propagate BadCredentialsException on invalid login")
        void shouldThrowException_whenAuthenticationFails() {

            LoginRequest request = new LoginRequest();
            request.setUsername("user");
            request.setPassword("wrong");

            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Invalid credentials"));

            assertThrows(BadCredentialsException.class, () ->
                    authService.login(request)
            );

            verify(jwtService, never()).generateToken(any());
        }
    }
}