package com.freelanceos.freelanceappback.application.rest;

import com.freelanceos.freelanceappback.application.rest.dto.user.UserRequest;
import com.freelanceos.freelanceappback.application.rest.dto.user.UserResponse;
import com.freelanceos.freelanceappback.application.rest.mapper.UserMapperRest;
import com.freelanceos.freelanceappback.domain.model.user.User;
import com.freelanceos.freelanceappback.domain.ports.in.user.CreateUserUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.user.DeleteUserUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.user.GetAllUsersUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.user.GetUserByIdUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.user.UpdateUserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final CreateUserUseCase createUserUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final UserMapperRest userMapperRest;

    public UserController(CreateUserUseCase createUserUseCase,
                          GetAllUsersUseCase getAllUsersUseCase,
                          GetUserByIdUseCase getUserByIdUseCase,
                          UpdateUserUseCase updateUserUseCase,
                          DeleteUserUseCase deleteUserUseCase,
                          UserMapperRest userMapperRest) {
        this.createUserUseCase = createUserUseCase;
        this.getAllUsersUseCase = getAllUsersUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.userMapperRest = userMapperRest;
    }

    @GetMapping
    public List<UserResponse> getUsers() {
        return getAllUsersUseCase.execute().stream()
                .map(userMapperRest::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return getUserByIdUseCase.execute(id)
                .map(userMapperRest::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody UserRequest userRequest) {
        User userToCreate = userMapperRest.toDomain(userRequest);
        return userMapperRest.toResponse(createUserUseCase.execute(userToCreate));
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        User userToUpdate = userMapperRest.toDomain(id, userRequest);
        return updateUserUseCase.execute(id, userToUpdate)
                .map(userMapperRest::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        boolean deleted = deleteUserUseCase.execute(id);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

}
