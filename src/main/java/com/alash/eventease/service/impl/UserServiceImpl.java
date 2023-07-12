package com.alash.eventease.service.impl;

import com.alash.eventease.dto.request.*;
import com.alash.eventease.dto.response.CustomResponse;
import com.alash.eventease.dto.response.UserRequestDto;
import com.alash.eventease.dto.response.UserResponseDto;
import com.alash.eventease.exception.UserAlreadyExistsException;
import com.alash.eventease.model.domain.UserEntity;
import com.alash.eventease.model.domain.UserRole;
import com.alash.eventease.repository.UserRepository;
import com.alash.eventease.repository.UserRoleRepository;
import com.alash.eventease.service.UserService;
import com.alash.eventease.utils.ResponseUtils;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]+$";
    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    public UserServiceImpl(UserRepository userRepository, UserRoleRepository userRoleRepository,
                           PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }


    @Override
    public ResponseEntity<CustomResponse> signup(UserRequestDto request) {
        ResponseEntity<CustomResponse> BAD_REQUEST = ChecksRequestValidity(request);
        if (BAD_REQUEST != null) return BAD_REQUEST;

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

    private ResponseEntity<CustomResponse> ChecksRequestValidity(UserRequestDto request) {

        if(userRepository.existsByEmail(request.getEmail())){
            throw new UserAlreadyExistsException("Email already exist");
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
        return null;
            }


    @Override
    public ResponseEntity<CustomResponse> signIn(LoginDto request) {
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
                .map(this::mapToUserResponse).collect(Collectors.toList());

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

    @Override
    public ResponseEntity<CustomResponse> verifyEmail(String token) {
        return null;
    }

    @Override
    public ResponseEntity<?> resendVerificationTokenEmail(String oldToken) throws MessagingException, UnsupportedEncodingException {
        return null;
    }

    @Override
    public ResponseEntity<CustomResponse> addAddress(Long userId, UserAddressRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<CustomResponse> deleteProfile(Long userId) {
        return null;
    }

    @Override
    public ResponseEntity<CustomResponse> updateProfile(Long userId, UserRequestDto request) {
        return null;
    }



    @Override
    public ResponseEntity<CustomResponse> uploadProfilePicture(Long userId, MultipartFile file) throws IOException {
        return null;
    }

    @Override
    public ResponseEntity<CustomResponse> fetchProfilePicture(Long userId) {
        return null;
    }

    @Override
    public ResponseEntity<CustomResponse> resetPassword(String email) throws MessagingException, UnsupportedEncodingException {
        return null;
    }

    @Override
    public ResponseEntity<CustomResponse> confirmResetPassword(Integer token, ResetPasswordDto request) {
        return null;
    }

    @Override
    public void saveVerificationToken(UserEntity theUser, String verificationToken) {

    }

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
