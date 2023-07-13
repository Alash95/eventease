package com.alash.eventease.service;

import com.alash.eventease.dto.response.CustomResponse;
import com.alash.eventease.dto.response.UserRequestDto;
import com.alash.eventease.exception.UserAlreadyExistsException;
import com.alash.eventease.model.domain.UserEntity;
import com.alash.eventease.model.domain.UserRole;
import com.alash.eventease.repository.IVerificationTokenRepository;
import com.alash.eventease.repository.UserRepository;
import com.alash.eventease.repository.UserRoleRepository;
import com.alash.eventease.service.impl.EmailService;
import com.alash.eventease.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserRoleRepository userRoleRepository;
    @Mock
    private IVerificationTokenRepository tokenRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private RedisTemplate redisTemplate;
    @Mock
    private HttpServletRequest servletRequest;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Captor
    private ArgumentCaptor<UserEntity> userArgumentCaptor;
    @Mock
    private UserService userService;
    @Mock
    private ApplicationEventPublisher publisher;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(
                userRepository, userRoleRepository,
                tokenRepository, emailService, publisher,
                redisTemplate, passwordEncoder, authenticationManager, servletRequest);
    }

    @Test
    void signup_ValidRequest_SuccessfulRegistration() {
        UserRequestDto request = new UserRequestDto();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("johndoe@example.com");
        request.setPhoneNumber("1234567890");
        request.setPassword("password123");

        UserEntity user = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .roles(Collections.singleton(new UserRole("ROLE_USER")))
                .password(passwordEncoder.encode(request.getPassword()))
                .isEnabled(false)
                .build();

        UserRole userRole = UserRole.builder()
                .id(1L)
                .name("ROLE_USER")
                .users(Collections.singleton(user))
                .build();

        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRoleRepository.findByName(anyString())).thenReturn(userRole);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

       ResponseEntity<CustomResponse> response =  userService.signup(request);

        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userRepository, times(1)).save(userArgumentCaptor.capture());
        UserEntity capturedUser = userArgumentCaptor.getValue();
        assertThat(response.getBody().getStatus()).isEqualTo("success");
        assertThat(capturedUser.getFirstName()).isEqualTo(user.getFirstName());
    }

    @Test
    void signup_UserAlreadyExists_ThrowsException() {
        //Creating and saving a user
        UserRequestDto request = new UserRequestDto();
        request.setFirstName("Caicedo");
        request.setLastName("Moses");
        request.setEmail("mocaicedo@example.com");
        request.setPhoneNumber("0234567890");
        request.setPassword("password123");

        UserEntity user = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .roles(Collections.singleton(new UserRole("ROLE_USER")))
                .password(passwordEncoder.encode(request.getPassword()))
                .isEnabled(false)
                .build();

        UserRole userRole = UserRole.builder()
                .id(1L)
                .name("ROLE_USER")
                .users(Collections.singleton(user))
                .build();

        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRoleRepository.findByName(anyString())).thenReturn(userRole);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        userService.signup(request);

        UserRequestDto request1 = UserRequestDto.builder()
                .firstName("Caicedo")
                .lastName("Moses")
                .email("mocaicedo@example.com")
                .phoneNumber("0234567890")
                .password("password123")
                .build();

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        verify(userRepository, atMost(1)).save(any(UserEntity.class));
        assertThrows(UserAlreadyExistsException.class, () -> userService.signup(request1));

    }

    // Additional tests for other error scenarios can be added similarly
}

