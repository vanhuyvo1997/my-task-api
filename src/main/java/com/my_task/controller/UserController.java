package com.my_task.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.my_task.service.user.UserRequest;
import com.my_task.service.user.UserService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("api/users")
@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getUser(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(userService.getUsers(query, size, page, sortDir));
    }

    @PutMapping("{id}/enabled")
    public ResponseEntity<?> enabled(@PathVariable String id) {
        return ResponseEntity.ok(userService.enabled(id));
    }

    @PutMapping("{id}/disabled")
    public ResponseEntity<?> disabled(@PathVariable String id) {
        return ResponseEntity.ok(userService.disabled(id));
    }
}
