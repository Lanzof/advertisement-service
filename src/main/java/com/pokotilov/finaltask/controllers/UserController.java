package com.pokotilov.finaltask.controllers;

import com.pokotilov.finaltask.dto.VoteDto;
import com.pokotilov.finaltask.dto.user.UpdateUserRequest;
import com.pokotilov.finaltask.dto.user.UserDto;
import com.pokotilov.finaltask.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<UserDto> getAllUsers(@ParameterObject Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") Long userId) {
        return userService.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable("userId") Long userId, Principal principal) {
        return userService.deleteUser(userId, principal);
    }

    @PostMapping("/{userId}")
    public String updateUser(@PathVariable("userId") Long id, @Valid @RequestBody UpdateUserRequest user, Principal principal) {
        return userService.updateUser(id, user, principal);
    }

    @PutMapping("/block")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String banUser(@RequestBody Long id) {
        return userService.banUser(id);
    }

    @PostMapping("/vote")
    public String voteUser(@Valid @RequestBody VoteDto voteDto, Principal principal) {
        return userService.voteUser(voteDto, principal);
    }
}
