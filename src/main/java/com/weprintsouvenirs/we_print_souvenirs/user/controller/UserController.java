package com.weprintsouvenirs.we_print_souvenirs.user.controller;

import com.weprintsouvenirs.we_print_souvenirs.order.dto.AllUserOrdersDTO;
import com.weprintsouvenirs.we_print_souvenirs.order.service.OrderService;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.*;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import com.weprintsouvenirs.we_print_souvenirs.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final OrderService orderService;

    public UserController(
            UserService userService,
            OrderService orderService
    ) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(
            @RequestBody UserRegisterDTO userRegisterDTO
    ) {
        UserEntity savedEntity = userService.registerUser(userRegisterDTO);
        UserResponseDTO response = new UserResponseDTO();
        response.setName(savedEntity.getName());
        response.setUsername(savedEntity.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(
            @RequestBody UserLoginDTO userLoginDTO
    ) {
        LoginResponseDTO response = userService.loginUser(userLoginDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequestDTO requestDTO
    ) {
        try {
            userService.changePassword(requestDTO);
            return ResponseEntity.status(HttpStatus.OK).body("Password changed");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileResponseDTO> getUserProfile(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserProfile());
    }

    @GetMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AllUserOrdersDTO>> getUserOrders() {
        List<AllUserOrdersDTO> orders = orderService.getOrdersForUser();
        return ResponseEntity.status(HttpStatus.OK).body(orders);
    }

    @PutMapping("/change-data")
    public ResponseEntity<?> changeUserData(
            @RequestBody ChangeUserDataRequestDTO requestDTO
    ) {
        try {
            ProfileResponseDTO updated = userService.changeUserData(requestDTO);
            return ResponseEntity.status(HttpStatus.OK).body(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
