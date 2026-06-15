package com.weprintsouvenirs.we_print_souvenirs.user.service;

import com.weprintsouvenirs.we_print_souvenirs.config.JwtUtil;
import com.weprintsouvenirs.we_print_souvenirs.order.model.OrderEntity;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.OrderItemRepository;
import com.weprintsouvenirs.we_print_souvenirs.order.repository.OrderRepository;
import com.weprintsouvenirs.we_print_souvenirs.user.Role;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.AdminUserResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.AdminUserUpdateRequestDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.ChangeUserDataRequestDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.LoginResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.ProfileResponseDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.UserLoginDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.dto.UserRegisterDTO;
import com.weprintsouvenirs.we_print_souvenirs.user.model.UserEntity;
import com.weprintsouvenirs.we_print_souvenirs.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private UserService userService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setName("Alice");
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setPhone("+100");
        user.setRole(Role.USER);
        user.setRegisteredAt(LocalDateTime.of(2026, 1, 1, 10, 0));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("alice", "password")
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registerUserEncodesPasswordAndSavesUserRole() {
        UserRegisterDTO request = new UserRegisterDTO();
        request.setName("Alice");
        request.setUsername("alice");
        request.setPassword("secret");
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("encoded");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserEntity saved = userService.registerUser(request);

        assertThat(saved.getName()).isEqualTo("Alice");
        assertThat(saved.getUsername()).isEqualTo("alice");
        assertThat(saved.getPassword()).isEqualTo("encoded");
        assertThat(saved.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void registerUserRejectsDuplicateUsername() {
        UserRegisterDTO request = new UserRegisterDTO();
        request.setUsername("alice");
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User with this username already exists");
    }

    @Test
    void loginUserAuthenticatesUpdatesLastLoginAndReturnsToken() {
        UserLoginDTO request = new UserLoginDTO();
        ReflectionTestUtils.setField(request, "username", "alice");
        ReflectionTestUtils.setField(request, "password", "secret");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(jwtUtil.generateToken("alice")).thenReturn("jwt-token");

        LoginResponseDTO response = userService.loginUser(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertThat(user.getLastLoginAt()).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUsername()).isEqualTo("alice");
        assertThat(response.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void getUserProfileReturnsCurrentUserData() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        ProfileResponseDTO profile = userService.getUserProfile();

        assertThat(profile.getName()).isEqualTo("Alice");
        assertThat(profile.getUsername()).isEqualTo("alice");
        assertThat(profile.getEmail()).isEqualTo("alice@example.com");
        assertThat(profile.getPhone()).isEqualTo("+100");
    }

    @Test
    void changeUserDataUpdatesOnlyNonBlankFields() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        ChangeUserDataRequestDTO request = new ChangeUserDataRequestDTO("Alice Smith", " ", "+200");

        ProfileResponseDTO profile = userService.changeUserData(request);

        assertThat(profile.getName()).isEqualTo("Alice Smith");
        assertThat(profile.getEmail()).isEqualTo("alice@example.com");
        assertThat(profile.getPhone()).isEqualTo("+200");
        verify(userRepository).save(user);
    }

    @Test
    void getAllUsersForAdminSortsById() {
        UserEntity second = new UserEntity();
        second.setId(2L);
        second.setUsername("bob");
        second.setName("Bob");
        when(userRepository.findAll()).thenReturn(List.of(second, user));

        List<AdminUserResponseDTO> users = userService.getAllUsersForAdmin();

        assertThat(users).hasSize(2);
        assertThat(users.get(0).getId()).isEqualTo(1L);
        assertThat(users.get(1).getId()).isEqualTo(2L);
    }

    @Test
    void updateUserForAdminRejectsDuplicateUsername() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsernameAndIdNot("taken", 1L)).thenReturn(true);
        AdminUserUpdateRequestDTO request = new AdminUserUpdateRequestDTO(null, "taken", null, null, null, null);

        assertThatThrownBy(() -> userService.updateUserForAdmin(1L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User with this username already exists");
    }

    @Test
    void updateUserForAdminAppliesProvidedFields() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsernameAndIdNot("manager", 1L)).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        AdminUserUpdateRequestDTO request = new AdminUserUpdateRequestDTO(
                "Manager", "manager", "manager@example.com", "+300", Role.ADMIN, "trusted"
        );

        AdminUserResponseDTO response = userService.updateUserForAdmin(1L, request);

        assertThat(response.getName()).isEqualTo("Manager");
        assertThat(response.getUsername()).isEqualTo("manager");
        assertThat(response.getEmail()).isEqualTo("manager@example.com");
        assertThat(response.getPhone()).isEqualTo("+300");
        assertThat(response.getAdminNote()).isEqualTo("trusted");
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void deleteUserForAdminDeletesOrdersItemsBeforeUser() {
        OrderEntity firstOrder = new OrderEntity();
        firstOrder.setId(1L);
        OrderEntity secondOrder = new OrderEntity();
        secondOrder.setId(2L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(orderRepository.findByUser(user)).thenReturn(List.of(firstOrder, secondOrder));

        userService.deleteUserForAdmin(1L);

        verify(orderItemRepository).deleteByOrder(firstOrder);
        verify(orderItemRepository).deleteByOrder(secondOrder);
        verify(orderRepository).delete(firstOrder);
        verify(orderRepository).delete(secondOrder);
        verify(userRepository).delete(user);
    }

    @Test
    void getUserForAdminMapsSingleUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        AdminUserResponseDTO response = userService.getUserForAdmin(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("alice");
    }
}
