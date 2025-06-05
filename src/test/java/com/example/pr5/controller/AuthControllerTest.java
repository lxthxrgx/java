package com.example.pr5.controller;

import com.example.pr5.DTO.SignupRequest;
import com.example.pr5.model.Role;
import com.example.pr5.model.User;
import com.example.pr5.repository.RoleRepository;
import com.example.pr5.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserRepository userRepo;

    @Mock
    private RoleRepository roleRepo;

    @Mock
    private PasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_SuccessfulRegistration_ReturnsCreated() {
        SignupRequest request = new SignupRequest();
        request.setUsername("newuser");
        request.setEmail("user@example.com");
        request.setPassword("password");
        request.setRoles(Set.of("ROLE_USER"));

        when(userRepo.existsByUsername("newuser")).thenReturn(false);
        when(userRepo.existsByEmail("user@example.com")).thenReturn(false);

        Role mockRole = new Role();
        mockRole.setName("ROLE_USER");
        when(roleRepo.findById("ROLE_USER")).thenReturn(Optional.of(mockRole));

        when(encoder.encode("password")).thenReturn("encodedPassword");

        ResponseEntity<?> response = authController.registerUser(request);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("User registered", response.getBody());
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_UsernameExists_ReturnsBadRequest() {
        SignupRequest request = new SignupRequest();
        request.setUsername("existinguser");
        request.setEmail("user@example.com");

        when(userRepo.existsByUsername("existinguser")).thenReturn(true);

        ResponseEntity<?> response = authController.registerUser(request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Username already exists", response.getBody());
        verify(userRepo, never()).save(any());
    }

    @Test
    void registerUser_RoleNotFound_ThrowsException() {
        SignupRequest request = new SignupRequest();
        request.setUsername("user");
        request.setEmail("email@example.com");
        request.setPassword("pass");
        request.setRoles(Set.of("ROLE_UNKNOWN"));

        when(userRepo.existsByUsername(any())).thenReturn(false);
        when(userRepo.existsByEmail(any())).thenReturn(false);
        when(roleRepo.findById("ROLE_UNKNOWN")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authController.registerUser(request);
        });

        assertEquals("Role not found: ROLE_UNKNOWN", exception.getMessage());
    }
}
