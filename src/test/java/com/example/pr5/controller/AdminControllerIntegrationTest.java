package com.example.pr5.controller;

import com.example.pr5.model.Role;
import com.example.pr5.model.User;
import com.example.pr5.repository.RoleRepository;
import com.example.pr5.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
public class AdminControllerIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepo;
    @Autowired private RoleRepository roleRepo;
    @Autowired private PasswordEncoder encoder;
    @Autowired private ObjectMapper objectMapper;

    private String jwtToken;
    private User testUser;

    @BeforeEach
    void setup() throws Exception {
        userRepo.deleteAll();
        roleRepo.deleteAll();

        Role roleUser = new Role(); roleUser.setName("ROLE_USER");
        Role roleAdmin = new Role(); roleAdmin.setName("ROLE_ADMIN");
        roleRepo.saveAll(Set.of(roleUser, roleAdmin));

        testUser = new User(null, "admin", "admin@mail.com", encoder.encode("password"), Set.of(roleAdmin));
        userRepo.save(testUser);

        String loginPayload = """
            {
              "username": "admin",
              "password": "password"
            }
            """;

        MvcResult result = mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginPayload))
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        jwtToken = objectMapper.readTree(responseBody).get("accessToken").asText();
    }

    @Test
    void updateRoles_withValidJwt_shouldUpdateUserRoles() throws Exception {
        String rolesPayload = objectMapper.writeValueAsString(Set.of("ROLE_USER"));

        mockMvc.perform(put("/api/admin/users/" + testUser.getId() + "/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rolesPayload)
                .header("Authorization", "Bearer " + jwtToken))
            .andExpect(status().isOk())
            .andExpect(content().string("Roles updated"));

        User updated = userRepo.findById(testUser.getId()).get();
        assert updated.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_USER"));
    }
}