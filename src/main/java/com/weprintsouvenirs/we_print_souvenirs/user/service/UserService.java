package com.weprintsouvenirs.we_print_souvenirs.user.service;

import com.weprintsouvenirs.we_print_souvenirs.config.JwtUtil;
import com.weprintsouvenirs.we_print_souvenirs.user.Role;
import com.weprintsouvenirs.we_print_souvenirs.user.User;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.*;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import com.weprintsouvenirs.we_print_souvenirs.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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

    /**
     * Метод регистрации нового пользователя
     * Принимает dto с информацией о регистрации.
     * Проверяет наличие пользователя с таким же username.
     *
     * @param dto
     * JSON:
     * {
     *     "username": "username",
     *     "password": "password",
     *     "email", "email"
     * }
     * @return
     * Сохранение в базу данных нового пользователя с информацией:
     * username, password (хэш), email, устанвливается роль по умолчанию USER
     */
    public UserEntity registerUser(UserRegisterDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("User with this username already exists");
        }

        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    /**
     * Метод для входа пользователя на сайт
     * Принимает DTO.
     * Проводит аутентификацию пользователя
     *
     * @param loginDTO
     * В DTO содержится введенные пользователем имя пользователя и пароль
     * JSON:
     * {
     *     "username": "username",
     *     "password": "password"
     * }
     * @return
     * JSON:
     * {
     *     "token": "JWTToken",
     *     "username": "username",
     *     "email": "email"
     * }
     */
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


    /**
     * Метод смены пароля пользователя
     * Для смены пароля пользователь должен ввести свой старый пароль и затем ввести новый
     *
     * @param requestDTO
     * JSON:
     * {
     *      "oldPassword": "oldPassword",
     *      "newPassword": "newPassword"
     * }
     */
    @Transactional
    public void changePassword(ChangePasswordRequestDTO requestDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(requestDTO.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        if (requestDTO.getNewPassword() == null || requestDTO.getNewPassword().isEmpty()) {
            throw new RuntimeException("New password cannot be empty");
        }

        user.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        userRepository.save(user);
    }


    /**
     * Метод для получения профиля пользователя
     * Метод достает из SecurityContex имя пользователя, ищет его в UserRepository
     * и возвращает его профиль
     *
     * @return
     * JSON:
     * {
     *      "username": "username",
     *      "email": "email",
     *      "phone": "phione"
     * }
     */
    @Transactional
    public ProfileResponseDTO getUserProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new ProfileResponseDTO(
                user.getUsername(),
                user.getEmail(),
                user.getPhone()
        );
    }

    /**
     * Метод для обновления личных данных пользователя: username, почта, номер телефона
     *
     * @param requestDTO Принимает DTO с новыми данными, которые пользователь ввел
     *                   JSON:
     *                   {
     *                      "username": "username",
     *                      "email": "email",
     *                      "phone": "+71234567890"
     *                   }
     * @return Возвращает DTO с новыми данными пользователя; формат JSON, такой же, как и принимаемый
     */
    @Transactional
    public ProfileResponseDTO changeUserData(ChangeUserDataRequestDTO requestDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (requestDTO.getUsername() != null && !requestDTO.getUsername().isBlank()) {
            if (!requestDTO.getUsername().equals(user.getUsername())
                    && userRepository.existsByUsername(requestDTO.getUsername())) {
                throw new RuntimeException("Username is already exists");
            }
            user.setUsername(requestDTO.getUsername());
        }

        if (requestDTO.getEmail() != null && !requestDTO.getEmail().isBlank()) {
            user.setEmail(requestDTO.getEmail());
        }

        if (requestDTO.getPhone() != null && !requestDTO.getPhone().isBlank()) {
            user.setPhone(requestDTO.getPhone());
        }

        userRepository.save(user);
        return new ProfileResponseDTO(
                user.getUsername(),
                user.getEmail(),
                user.getPhone()
        );
    }
}


