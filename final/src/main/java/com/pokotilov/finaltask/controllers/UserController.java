package com.pokotilov.finaltask.controllers;

import com.pokotilov.finaltask.dto.user.UpdateUserRequest;
import com.pokotilov.finaltask.dto.user.UserDto;
import com.pokotilov.finaltask.services.user.UserService;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Методы для взаимодействия с пользователями.")
@SecurityRequirement(name = "bearerAuth")
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping
    public Page<UserDto> getAllUsers(
            @Schema(description = "№ страницы. (1..N)", type = "integer", defaultValue = "1") @NotNull @Positive Integer pageNo,
            @Schema(description = "Размер страницы.", minimum = "1", type = "integer", defaultValue = "10") @NotNull @Positive Integer pageSize) {
        return userService.getAllUsers(pageNo, pageSize);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") @Positive Long userId) {
        return userService.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") @Positive Long userId, Principal principal) {
        return ResponseEntity.ok().body(userService.deleteUser(userId, principal));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("userId") @Positive Long id, @Valid @RequestBody UpdateUserRequest user, Principal principal) {
        return ResponseEntity.ok().body(userService.updateUser(id, user, principal));
    }

    @PutMapping("/{userId}/ban")
    @Secured({ "ROLE_ADMIN" })
    public ResponseEntity<String> banUser(@PathVariable("userId") @Positive Long id) {
        return ResponseEntity.ok().body(userService.banUser(id));
    }
}
