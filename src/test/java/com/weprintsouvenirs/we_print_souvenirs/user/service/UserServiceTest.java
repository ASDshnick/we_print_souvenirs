package com.weprintsouvenirs.we_print_souvenirs.user.service;

import com.weprintsouvenirs.we_print_souvenirs.config.JwtUtil;
import com.weprintsouvenirs.we_print_souvenirs.testsupport.TestData;
import com.weprintsouvenirs.we_print_souvenirs.user.Role;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.*;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import com.weprintsouvenirs.we_print_souvenirs.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registerUserCreatesUserWithEncodedPasswordAndUserRole() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("client");
        dto.setPassword("plain");
        dto.setEmail("client@example.com");

        when(userRepository.existsByUsername("client")).thenReturn(false);
        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserEntity result = userService.registerUser(dto);

        assertThat(result.getUsername()).isEqualTo("client");
        assertThat(result.getPassword()).isEqualTo("encoded");
        assertThat(result.getEmail()).isEqualTo("client@example.com");
        assertThat(result.getRole()).isEqualTo(Role.USER);
        verify(userRepository).save(result);
    }

    @Test
    void registerUserRejectsDuplicateUsername() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("client");

        when(userRepository.existsByUsername("client")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User with this username already exists");
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginUserAuthenticatesAndReturnsToken() {
        UserLoginDTO dto = new UserLoginDTO();
        ReflectionTestUtils.setField(dto, "username", "client");
        ReflectionTestUtils.setField(dto, "password", "plain");

        UserEntity user = TestData.user(1L, "client", Role.USER);
        when(userRepository.findByUsername("client")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("client")).thenReturn("jwt-token");

        LoginResponseDTO response = userService.loginUser(dto);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUsername()).isEqualTo("client");
        assertThat(response.getEmail()).isEqualTo("client@example.com");
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void changePasswordUpdatesPasswordWhenOldPasswordMatches() {
        TestData.authenticateAs("client");
        UserEntity user = TestData.user(1L, "client", Role.USER);
        ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO("old", "new");

        when(userRepository.findByUsername("client")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", "encoded-password")).thenReturn(true);
        when(passwordEncoder.encode("new")).thenReturn("encoded-new");

        userService.changePassword(dto);

        assertThat(user.getPassword()).isEqualTo("encoded-new");
        verify(userRepository).save(user);
    }

    @Test
    void changePasswordRejectsIncorrectOldPassword() {
        TestData.authenticateAs("client");
        UserEntity user = TestData.user(1L, "client", Role.USER);
        when(userRepository.findByUsername("client")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded-password")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(new ChangePasswordRequestDTO("wrong", "new")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Old password is incorrect");
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePasswordRejectsEmptyNewPassword() {
        TestData.authenticateAs("client");
        UserEntity user = TestData.user(1L, "client", Role.USER);
        when(userRepository.findByUsername("client")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", "encoded-password")).thenReturn(true);

        assertThatThrownBy(() -> userService.changePassword(new ChangePasswordRequestDTO("old", "")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("New password cannot be empty");
    }

    @Test
    void getUserProfileReturnsCurrentUserData() {
        TestData.authenticateAs("client");
        UserEntity user = TestData.user(1L, "client", Role.USER);
        when(userRepository.findByUsername("client")).thenReturn(Optional.of(user));

        ProfileResponseDTO response = userService.getUserProfile();

        assertThat(response.getUsername()).isEqualTo("client");
        assertThat(response.getEmail()).isEqualTo("client@example.com");
        assertThat(response.getPhone()).isEqualTo("+79990000000");
    }

    @Test
    void changeUserDataUpdatesAllowedFields() {
        TestData.authenticateAs("client");
        UserEntity user = TestData.user(1L, "client", Role.USER);
        ChangeUserDataRequestDTO dto = new ChangeUserDataRequestDTO("new-client", "new@example.com", "+70001112233");

        when(userRepository.findByUsername("client")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("new-client")).thenReturn(false);

        ProfileResponseDTO response = userService.changeUserData(dto);

        assertThat(response.getUsername()).isEqualTo("new-client");
        assertThat(response.getEmail()).isEqualTo("new@example.com");
        assertThat(response.getPhone()).isEqualTo("+70001112233");

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo("new-client");
    }

    @Test
    void changeUserDataRejectsTakenUsername() {
        TestData.authenticateAs("client");
        UserEntity user = TestData.user(1L, "client", Role.USER);
        when(userRepository.findByUsername("client")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("taken")).thenReturn(true);

        assertThatThrownBy(() -> userService.changeUserData(new ChangeUserDataRequestDTO("taken", null, null)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Username is already exists");
    }
}
