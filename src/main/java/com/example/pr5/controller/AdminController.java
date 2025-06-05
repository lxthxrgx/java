package com.example.pr5.controller;

import com.example.pr5.model.User;
import com.example.pr5.model.Role;
import com.example.pr5.repository.UserRepository;
import com.example.pr5.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @GetMapping("/users")
    public List<User> allUsers() {
        return userRepo.findAll().stream()
            .peek(u -> u.setPassword(null))
            .collect(Collectors.toList());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return userRepo.findById(id)
            .map(u -> {
                u.setPassword(null);
                return ResponseEntity.ok(u);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}/roles")
    public ResponseEntity<?> updateRoles(@PathVariable Long id, @RequestBody Set<String> roles) {
        User user = userRepo.findById(id).orElseThrow();
        Set<Role> newRoles = roles.stream()
            .map(r -> roleRepo.findById(r).orElseThrow())
            .collect(Collectors.toSet());
        user.setRoles(newRoles);
        userRepo.save(user);
        return ResponseEntity.ok("Roles updated");
    }
}

