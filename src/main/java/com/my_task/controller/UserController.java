package com.my_task.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(userService.getUsers(query, pageSize, pageNum, sortDir));
    }

    @PutMapping("{id}/enabled")
    public ResponseEntity<?> enabled(@PathVariable String id) {
        return ResponseEntity.ok(userService.enabled(id));
    }

    @PutMapping("{id}/disabled")
    public ResponseEntity<?> disabled(@PathVariable String id) {
        return ResponseEntity.ok(userService.disabled(id));
    }

    @GetMapping("top")
    public ResponseEntity<?> getTop5(@RequestParam(required = false, defaultValue = "1") int topNum) {
        return ResponseEntity.ok().body(userService.getTopAtiveUsers(topNum));
    }

}
