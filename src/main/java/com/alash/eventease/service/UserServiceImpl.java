package com.alash.eventease.service;

import com.alash.eventease.dto.*;
import com.alash.eventease.model.UserEntity;
import com.alash.eventease.model.UserRole;
import com.alash.eventease.repository.UserRepository;
import com.alash.eventease.repository.UserRoleRepository;
import com.alash.eventease.security.JwtTokenProvider;
import com.alash.eventease.utils.ResponseUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]+$";
    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);
    @Override
    public ResponseEntity<CustomResponse> signup(UserRequestDto request) {
        boolean existsByEmail = userRepository.existsByEmail(request.getEmail());

        if(existsByEmail){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "User already exists"));
        }
        if(request == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "Request body is required"));
        }
        if(request.getFirstName() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "firstName is required"));
        }
        if(request.getLastName() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "lastName is required"));
        }
        if(request.getEmail() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "email is required"));
        }
        if(!validateEmail(request.getEmail())){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "provide correct email format"));
        }
        if(request.getPhoneNumber() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "password is required"));
        }
        if(request.getPassword() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "password is required"));
        }

        UserRole role = userRoleRepository.findByName("ROLE_USER");
        UserEntity user = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .roles(Collections.singleton(role))
                .password(passwordEncoder.encode(request.getPassword()))
                .isEnabled(false)
                .build();
        userRepository.save(user);

        return ResponseEntity.ok().body(new CustomResponse(HttpStatus.CREATED, "User registered successfully"));

    }

    @Override
    public ResponseEntity<CustomResponse> signIn(LoginDto request) {
////authenticating user here
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())
//        );
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        AuthResponse authResponse = new AuthResponse();
//        authResponse.setToken(jwtTokenProvider.generateToken(authentication));
//        return authResponse;
Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        LoginDto login = new LoginDto();
                login.setEmail(request.getEmail());
                login.setPassword(request.getPassword());

        return ResponseEntity.ok().body(new CustomResponse(HttpStatus.OK, ResponseUtils.SUCCESSFUL_LOGIN_MESSAGE));

    }



    @Override
    public ResponseEntity<CustomResponse> changePassword(ChangePasswordDto request) {
        if(request.getEmail() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "email is required"));
        }
        if(!validateEmail(request.getEmail())){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "provide correct email format"));
        }
        if(request.getOldPassword() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "oldPassword is required"));
        }
        if(request.getNewPassword() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "newPassword is required"));
        }
        Optional<UserEntity> userOpt = userRepository.findByEmail(request.getEmail());

        if(userOpt.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "No user is associated with this email"));
        }

        UserEntity user = userOpt.get();
        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "Old password is not correct. Try again"));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK, ResponseUtils.SUCCESSFULLY_RESET_PASSWORD_MESSAGE));
    }



    @Override
    public ResponseEntity<CustomResponse> fetchAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        List<UserResponseDto> userResponseList = users.stream()
                .map(user -> mapToUserResponse(user)).collect(Collectors.toList());

        CustomResponse successResponse = CustomResponse.builder()
                .status(HttpStatus.OK.name())
                .message("Successful")
                .data(userResponseList.isEmpty() ? null : userResponseList)
                .build();

        return ResponseEntity.ok(successResponse);
    }

    @Override
    public ResponseEntity<CustomResponse> fetchSingleUser(FetchUserRequest request) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(request.getEmail());
        if(userOpt.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "No user with email address found"));
        }
        UserEntity user = userOpt.get();
        UserResponseDto response = UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .role(new HashSet<>(user.getRoles()))
                .isEnabled(user.isEnabled())
                .build();
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), response, "Successful"));    }

    public static boolean validateEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private UserResponseDto mapToUserResponse(UserEntity user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .phoneNumber(user.getPhoneNumber())
                .role(new HashSet<>(user.getRoles()))
                .isEnabled(user.isEnabled())
                .build();
    }
}
