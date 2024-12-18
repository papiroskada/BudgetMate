package com.example.PersonalAccounting.controllers;

import com.example.PersonalAccounting.dto.UserDTO;
import com.example.PersonalAccounting.entity.User;
import com.example.PersonalAccounting.services.entity_service_impl.UserService;
import com.example.PersonalAccounting.util.dto_entity_converter.UserDtoConverter;
import com.example.PersonalAccounting.util.response.ErrorResponse;
import com.example.PersonalAccounting.util.validators.UserValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.example.PersonalAccounting.util.ErrorsUtil.generateErrorMessage;

@RestController
@RequestMapping("/auth")
@EnableMethodSecurity
public class AuthController {

    private final UserService userService;
    private final UserDtoConverter userDtoConverter;
    private final UserValidator userValidator;

    @Autowired
    public AuthController(UserService userService, UserDtoConverter userDtoConverter, UserValidator userValidator) {
        this.userService = userService;
        this.userDtoConverter = userDtoConverter;

        this.userValidator = userValidator;
    }

    @PostMapping("/registration")
    public ResponseEntity<HttpStatus> registration(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        User user = userDtoConverter.convertToUser(userDTO);
        userValidator.validate(user, bindingResult);

        if(bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateErrorMessage(bindingResult.getFieldErrors()));
        }

        userService.create(user);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public UserDTO getAuthenticated(@AuthenticationPrincipal UserDetails userDetails) {
        return userDtoConverter.convertToUserDTO(userService.getOne(userDetails.getUsername()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
        ErrorResponse response = new ErrorResponse(
                e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
