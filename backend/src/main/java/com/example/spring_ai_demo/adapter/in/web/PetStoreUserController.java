package com.example.spring_ai_demo.adapter.in.web;


import com.example.petstore.server.api.UserApi;
import com.example.petstore.server.model.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pet-store")
public class PetStoreUserController implements UserApi {
    @Override
    public ResponseEntity<Void> createUser(User user) {
        return null;
    }

    @Override
    public ResponseEntity<Void> createUsersWithArrayInput(List<@Valid User> user) {
        return null;
    }

    @Override
    public ResponseEntity<Void> createUsersWithListInput(List<@Valid User> user) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteUser(String username) {
        return null;
    }

    @Override
    public ResponseEntity<User> getUserByName(String username) {
        return null;
    }

    @Override
    public ResponseEntity<String> loginUser(String username, String password) {
        return null;
    }

    @Override
    public ResponseEntity<Void> logoutUser() {
        return null;
    }

    @Override
    public ResponseEntity<Void> updateUser(String username, User user) {
        return null;
    }
}
