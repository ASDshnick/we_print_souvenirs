package com.weprintsouvenirs.we_print_souvenirs.user.service;

import com.weprintsouvenirs.we_print_souvenirs.config.JwtUtil;
import com.weprintsouvenirs.we_print_souvenirs.user.Role;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.ChangePasswordRequestDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.LoginResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.UserLoginDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.UserRegisterDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import com.weprintsouvenirs.we_print_souvenirs.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    JwtUtil jwtUtil;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public UserEntity registerUser(UserRegisterDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("User with this username already exists");
        }

        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
//        dto.setRole(Role.USER);
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    public LoginResponseDTO loginUser(UserLoginDTO loginDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword()
                )
        );

        UserEntity user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getUsername());
        return new LoginResponseDTO(token, user.getUsername(), user.getEmail());
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequestDTO requestDTO) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (!passwordEncoder.matches(requestDTO.getOldPassword(), user.getPassword())){
            throw new RuntimeException("Old password is incorrect");
        }

        if (requestDTO.getNewPassword() == null || requestDTO.getNewPassword().isEmpty()){
            throw new RuntimeException("New password cannot be empty");
        }

        user.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        userRepository.save(user);
    }

//    private User toDomainUser(
//            UserEntity userEntity
//    ) {
//        return new User(
//                userEntity.getId(),
//                userEntity.getUsername(),
//                userEntity.getPassword(),
//                userEntity.getEmail(),
//                userEntity.getPhone(),
//                userEntity.getTelegram()
//        );
//    }
}


