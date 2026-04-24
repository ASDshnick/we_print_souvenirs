package com.weprintsouvenirs.we_print_souvenirs.user.controller;

import com.weprintsouvenirs.we_print_souvenirs.user.dto.LoginResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.UserLoginDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.UserRegisterDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.UserResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import com.weprintsouvenirs.we_print_souvenirs.user.service.UserService;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser (
            @RequestBody UserRegisterDTO userRegisterDTO
    ) {
        UserEntity savedEntity = userService.registerUser(userRegisterDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();

//        UserResponseDTO response = new UserResponseDTO();
//        response.setUsername(savedEntity.getUsername());
//        response.setEmail(savedEntity.getEmail());
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser (
            @RequestBody UserLoginDTO userLoginDTO
    ) {
        LoginResponseDTO response = userService.loginUser(userLoginDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
