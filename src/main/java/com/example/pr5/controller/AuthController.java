package com.example.pr5.controller;

import com.example.pr5.DTO.JwtResponse;
import com.example.pr5.DTO.LoginRequest;
import com.example.pr5.DTO.SignupRequest;
import com.example.pr5.impl.UserDetailsServiceImpl;
import com.example.pr5.jwt.JwtUtils;
import com.example.pr5.model.Role;
import com.example.pr5.model.User;
import com.example.pr5.repository.RoleRepository;
import com.example.pr5.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepo;

    @Autowired
    RoleRepository roleRepo;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest request) {
        if (userRepo.existsByUsername(request.getUsername()))
            return ResponseEntity.badRequest().body("Username already exists");

        if (userRepo.existsByEmail(request.getEmail()))
            return ResponseEntity.badRequest().body("Email already exists");

        Set<Role> roles = new HashSet<>();
        for (String roleName : request.getRoles()) {
            roles.add(roleRepo.findById(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)));
        }

        User user = new User(null, request.getUsername(), request.getEmail(), encoder.encode(request.getPassword()), roles);
        userRepo.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest request) {
        Authentication authentication = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(userDetails);
        User user = userRepo.findByUsername(userDetails.getUsername()).get();

        List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt, "Bearer", user.getId(), user.getUsername(), user.getEmail(), roles)
);

    }
}

