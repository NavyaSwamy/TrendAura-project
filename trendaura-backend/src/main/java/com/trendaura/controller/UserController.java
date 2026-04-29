package com.trendaura.controller;

import com.trendaura.dto.UserResponse;
import com.trendaura.dto.UserUpdateRequest;
import com.trendaura.service.UserService;
import com.trendaura.util.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getProfile() {
        return ResponseEntity.ok(userService.getProfile(CurrentUser.id()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(@Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateProfile(CurrentUser.id(), request));
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(@RequestBody Map<String, String> body) {
        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");
        if (currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().build();
        }
        userService.changePassword(CurrentUser.id(), currentPassword, newPassword);
        return ResponseEntity.ok().build();
    }
}
