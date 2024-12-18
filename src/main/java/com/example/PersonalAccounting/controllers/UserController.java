package com.example.PersonalAccounting.controllers;

import com.example.PersonalAccounting.dto.UserDTO;
import com.example.PersonalAccounting.entity.User;
import com.example.PersonalAccounting.services.entity_service_impl.UserService;
import com.example.PersonalAccounting.util.dto_entity_converter.UserDtoConverter;
import com.example.PersonalAccounting.util.response.ErrorResponse;
import com.example.PersonalAccounting.util.validators.PasswordMatch;
import com.example.PersonalAccounting.util.validators.UserValidator;
import com.example.PersonalAccounting.util.validators.id_match.UserIdMatch;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.example.PersonalAccounting.util.ErrorsUtil.generateErrorMessage;

@RestController
@RequestMapping("/users")
public class UserController {

    //TODO: try to get UserDetailsImpl with @AuthenticationPrincipal


    private final UserService userService;
    private final PasswordMatch passwordMatch;
    private final UserIdMatch userIdMatch;
    private final UserDtoConverter userDtoConverter;
    private final UserValidator userValidator;

    @Autowired
    public UserController(UserService userService, PasswordMatch passwordMatch, UserIdMatch userIdMatch,
                          UserDtoConverter userDtoConverter, UserValidator userValidator) {
        this.userService = userService;
        this.passwordMatch = passwordMatch;
        this.userIdMatch = userIdMatch;
        this.userDtoConverter = userDtoConverter;
        this.userValidator = userValidator;
    }

    @GetMapping
    public List<UserDTO> getAll() {
        return userService.getAll().stream().map(userDtoConverter::convertToUserDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDTO getOne(@PathVariable int id) {
        userIdMatch.matchUserId(id);
        return userDtoConverter.convertToUserDTO(userService.getOne(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@PathVariable int id, @RequestBody @Valid UserDTO userDTO,
                                             BindingResult bindingResult) {
        userIdMatch.matchUserId(id);

        User user = userDtoConverter.convertToUser(userDTO);
        userValidator.validate(user, bindingResult);

        if(bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateErrorMessage(bindingResult.getFieldErrors()));
        }

        userService.update(id, user);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}/update-password")
    public ResponseEntity<HttpStatus> updatePassword(@PathVariable int id,
                                                     @RequestBody Map<String, String> passwords,
                                                     @AuthenticationPrincipal UserDetails userDetails,
                                                     BindingResult bindingResult) {
        userIdMatch.matchUserId(id);
        passwordMatch.match(passwords.get("oldPassword"), userDetails.getPassword(), bindingResult);

        if(bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateErrorMessage(bindingResult.getFieldErrors()));
        }

        userService.updatePassword(id, passwords.get("newPassword"));
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable int id) {
        userIdMatch.matchUserId(id);
        userService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(NoSuchElementException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
