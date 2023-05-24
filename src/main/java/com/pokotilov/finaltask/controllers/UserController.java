package com.pokotilov.finaltask.controllers;

import com.pokotilov.finaltask.dto.VoteDto;
import com.pokotilov.finaltask.dto.user.UpdateUserRequest;
import com.pokotilov.finaltask.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Методы для взаимодействия с пользователями.")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

//    @GetMapping("/paged")
//    public ResponseEntity<?> getAllUsers() {
//        return ResponseEntity.ok(userService.getAllUsers().getList());
//    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable).getList());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.getUser(userId).getList());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId, Principal principal) {
        return ResponseEntity.ok(userService.deleteUser(userId, principal).getMessage());
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable("userId") Long id, @Valid @RequestBody UpdateUserRequest user, Principal principal) {
        return ResponseEntity.ok(userService.updateUser(id, user, principal).getMessage());
    }

    @PutMapping("/block")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> banUser(@RequestBody Long id) {
        return ResponseEntity.ok(userService.banUser(id).getMessage());
    }

    @PostMapping("/vote")
    public ResponseEntity<?> voteUser(@Valid @RequestBody VoteDto voteDto, Principal principal) {
        return ResponseEntity.ok(userService.voteUser(voteDto, principal).getMessage());
    }
}
