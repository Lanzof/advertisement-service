package com.pokotilov.finaltask.controllers;

import com.pokotilov.finaltask.dto.UserDto;
import com.pokotilov.finaltask.dto.VoteDto;
import com.pokotilov.finaltask.services.UserServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    private final UserServiceImpl userService;

//    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers().getList());
    }

    @GetMapping("/paged")
    public ResponseEntity<?> getAllUsers(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable).getList());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> profile(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.getUser(userId).getList());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.deleteUser(userId).getMessage());
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable("userId") Long id, @RequestBody UserDto user) {
        return ResponseEntity.ok(userService.updateUser(id, user).getMessage());
    }//TODO reauthorize after changing password
    //todo make standalone update dto

    @PutMapping("/block")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> blockUser(@RequestBody Long id) {
        return ResponseEntity.ok(userService.blockUser(id).getMessage());
    }

    @PostMapping("/vote")
    public ResponseEntity<?> voteUser(@RequestBody VoteDto voteDto, Principal principal) {
        return ResponseEntity.ok(userService.voteUser(voteDto, principal).getMessage());
    }
}
