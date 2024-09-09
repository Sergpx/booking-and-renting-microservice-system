package org.sergp.userservice.controllers;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sergp.userservice.dto.UpdateUser;
import org.sergp.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUser updateUser,
                                        @PathVariable UUID id,
                                        HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(updateUser, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@Valid @PathVariable UUID id,
                                        HttpServletRequest request){
        userService.deleteUser(id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Deleted successfully!");
    }


}
