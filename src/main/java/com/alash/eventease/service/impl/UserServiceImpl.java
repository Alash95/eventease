package com.alash.eventease.service.impl;

import com.alash.eventease.dto.request.*;
import com.alash.eventease.dto.response.CustomResponse;
import com.alash.eventease.dto.response.Response;
import com.alash.eventease.dto.response.UserRequestDto;
import com.alash.eventease.dto.response.UserResponseDto;
import com.alash.eventease.event.RegistrationCompletePublisher;
import com.alash.eventease.exception.UserAlreadyExistsException;
import com.alash.eventease.model.domain.UserEntity;
import com.alash.eventease.model.domain.UserRole;
import com.alash.eventease.model.domain.VerificationToken;
import com.alash.eventease.repository.IVerificationTokenRepository;
import com.alash.eventease.repository.UserRepository;
import com.alash.eventease.repository.UserRoleRepository;
import com.alash.eventease.service.UserService;
import com.alash.eventease.utils.ResponseUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final IVerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final ApplicationEventPublisher publisher;
    private final RedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final HttpServletRequest servletRequest;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]+$";
    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);
    private static final String PASSWORD_RESET = "code";


//    public UserServiceImpl(UserRepository userRepository, UserRoleRepository userRoleRepository, IVerificationTokenRepository tokenRepository, EmailService emailService, ApplicationEventPublisher publisher, RedisTemplate redisTemplate, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, HttpServletRequest servletRequest) {
//        this.userRepository = userRepository;
//        this.userRoleRepository = userRoleRepository;
//        this.tokenRepository = tokenRepository;
//        this.emailService = emailService;
//        this.publisher = publisher;
//        this.redisTemplate = redisTemplate;
//        this.passwordEncoder = passwordEncoder;
//        this.authenticationManager = authenticationManager;
//        this.servletRequest = servletRequest;
//    }

    @Override
    public ResponseEntity<CustomResponse> signup(UserRequestDto request) {
        ResponseEntity<CustomResponse> BAD_REQUEST = ChecksRequestValidity(request);
        if (BAD_REQUEST != null) return BAD_REQUEST;

        UserRole role = userRoleRepository.findByName("ROLE_USER");
        UserEntity newUser = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .roles(Collections.singleton(role))
                .password(passwordEncoder.encode(request.getPassword()))
                .isEnabled(false)
                .build();

        UserEntity savedUser = userRepository.save(newUser);

        Response response = Response.builder()
                .responseCode(ResponseUtils.SUCCESS)
                .responseMessage(ResponseUtils.USER_REGISTERED_SUCCESS)
                .data(UserResponseDto.builder()
                        .fullName(savedUser.getFirstName() + " " + savedUser.getLastName())
                        .email(savedUser.getEmail())
                        .phoneNumber(savedUser.getPhoneNumber())
                        .role(Collections.singleton(role))
                        .isEnabled(savedUser.isEnabled())
                        .build())
                .build();

        publisher.publishEvent(new RegistrationCompletePublisher(newUser, applicationUrl(servletRequest)));

        CustomResponse customResponse = new CustomResponse(HttpStatus.OK, "User registered successfully");
        customResponse.setData(response);
        return ResponseEntity.ok().body(customResponse);

    }

    @Override
    public ResponseEntity<CustomResponse> signupAdmin(UserRequestDto request) {
        ResponseEntity<CustomResponse> BAD_REQUEST = ChecksRequestValidity(request);
        if (BAD_REQUEST != null) return BAD_REQUEST;

        UserRole role = userRoleRepository.findByName("ROLE_ADMIN");
        UserEntity newUser = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .roles(Collections.singleton(role))
                .password(passwordEncoder.encode(request.getPassword()))
                .isEnabled(false)
                .build();

        UserEntity savedUser = userRepository.save(newUser);

        Response response = Response.builder()
                .responseCode(ResponseUtils.SUCCESS)
                .responseMessage(ResponseUtils.USER_REGISTERED_SUCCESS)
                .data(UserResponseDto.builder()
                        .fullName(savedUser.getFirstName() + " " + savedUser.getLastName())
                        .email(savedUser.getEmail())
                        .phoneNumber(savedUser.getPhoneNumber())
                        .role(Collections.singleton(role))
                        .isEnabled(savedUser.isEnabled())
                        .build())
                .build();

        publisher.publishEvent(new RegistrationCompletePublisher(newUser, applicationUrl(servletRequest)));

        CustomResponse customResponse = new CustomResponse(HttpStatus.OK, "Admin registered successfully");
        customResponse.setData(response);
        return ResponseEntity.ok().body(customResponse);

    }

    private ResponseEntity<CustomResponse> ChecksRequestValidity(UserRequestDto request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exist");
        }
        if (request == null) {
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "Request body is required"));
        }
        if (request.getFirstName() == null) {
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "firstName is required"));
        }
        if (request.getLastName() == null) {
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "lastName is required"));
        }
        if (request.getEmail() == null) {
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "email is required"));
        }
        if (!validateEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "provide correct email format"));
        }
        if (request.getPhoneNumber() == null) {
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "password is required"));
        }
        if (request.getPassword() == null) {
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "password is required"));
        }
        return null;
    }


    @Override
    public ResponseEntity<CustomResponse> signInUser(LoginDto request) {
        CustomResponse response = new CustomResponse();
        Optional<UserEntity> userOpt = userRepository.findByEmail(request.getEmail());
        if (!userOpt.isEmpty()){
            boolean checkPassword = BCrypt.checkpw(request.getPassword(), userOpt.get().getPassword());
            if (!checkPassword){
                response.setMessage("Invalid password");
                return ResponseEntity.badRequest().body(response);
            }
            else {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                response.setData(userOpt.get());
                return ResponseEntity.ok().body(response);
            }
        }
        else {
            response.setMessage("User with this email address doesn't exist");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Override
    public ResponseEntity<CustomResponse> loginUser(LoginDto request) {
        CustomResponse response = new CustomResponse();
        Optional<UserEntity> userOpt = userRepository.findByEmail(request.getEmail());
        if (!userOpt.isEmpty()){
        boolean checkPassword = BCrypt.checkpw(request.getPassword(), userOpt.get().getPassword());
        if (!checkPassword){
            response.setMessage("Invalid password");
            return ResponseEntity.badRequest().body(response);
        }
        else {
            response.setData(userOpt.get());
            return ResponseEntity.ok().body(response);
        }
        }
        else {
            response.setMessage("User with this email address doesn't exist");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Override
    public ResponseEntity<CustomResponse> signInAdmin(LoginDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        LoginDto login = new LoginDto();
        login.setEmail(request.getEmail());
        login.setPassword(request.getPassword());


        Response response = Response.builder()
                .responseCode(ResponseUtils.SUCCESS)
                .responseMessage(ResponseUtils.USER_REGISTERED_SUCCESS)
                .data(UserResponseDto.builder()
                        .email(request.getEmail())
                        .build())
                .build();

        CustomResponse customResponse = new CustomResponse(HttpStatus.OK, ResponseUtils.SUCCESSFUL_LOGIN_MESSAGE);
        customResponse.setData(response);
        return ResponseEntity.ok().body(customResponse);    }


    @Override
    public ResponseEntity<CustomResponse> changePassword(ChangePasswordDto request) {
        if (request.getEmail() == null) {
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "email is required"));
        }
        if (!validateEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "provide correct email format"));
        }
        if (request.getOldPassword() == null) {
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "oldPassword is required"));
        }
        if (request.getNewPassword() == null) {
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "newPassword is required"));
        }
        Optional<UserEntity> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "No user is associated with this email"));
        }

        UserEntity user = userOpt.get();
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "Old password is not correct. Try again"));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        UserEntity users = userRepository.save(user);

        CustomResponse customResponse = new CustomResponse(HttpStatus.OK, ResponseUtils.SUCCESSFULLY_RESET_PASSWORD_MESSAGE);
        customResponse.setData(users);
        return ResponseEntity.ok().body(customResponse);
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
        return ResponseEntity.ok().body(successResponse);
    }

    @Override
    public ResponseEntity<CustomResponse> fetchUserById(Long userId) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "No user found"));
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
        return ResponseEntity.ok().body(new CustomResponse(HttpStatus.OK.name(), response, "Successful"));
    }

    @Override
    public ResponseEntity<CustomResponse> fetchSingleUser(String email) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
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
        return ResponseEntity.ok().body(new CustomResponse(HttpStatus.OK.name(), response, "Successful"));
    }

    @Override
    public ResponseEntity<CustomResponse> verifyEmail(String token) {
        String url = applicationUrl(servletRequest)+"/api/v1/register/resend-token?token="+token;
        log.info("Resend link {} ", url);
        Optional<VerificationToken> tokenOpt = tokenRepository.findByToken(token);
        if(!tokenOpt.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "No token found"));
        }
        VerificationToken theToken = tokenOpt.get();
        if(theToken.getUser().isEnabled()){
            return ResponseEntity.ok().body(new CustomResponse(HttpStatus.FOUND, "This user has already been verified, please login"));
        }
        String message = "<p> Link has expired.<a href=\"" + url + "\">Get a new verification link</a></p>";
        CustomResponse verificationResult = validateToken(token);
        if(verificationResult.getMessage().equalsIgnoreCase("Valid")){
            return ResponseEntity.ok().body(new CustomResponse(HttpStatus.OK, "Email verified successfully. Kindly proceed to login"));
        }
        CustomResponse response = new CustomResponse(HttpStatus.BAD_REQUEST, message);
        return ResponseEntity.badRequest().body(response);    }

    @Override
    public CustomResponse validateToken(String token) {
        Optional<VerificationToken> tokeOpt = tokenRepository.findByToken(token);
        if (!tokeOpt.isPresent()) {
            return new CustomResponse(HttpStatus.BAD_REQUEST, "Invalid verification token");
        }
        VerificationToken theToken = tokeOpt.get();

        UserEntity user = theToken.getUser();
        Calendar calendar = Calendar.getInstance();
        if ((theToken.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
            //tokenRepository.delete(theToken);
            return new CustomResponse(HttpStatus.BAD_REQUEST, "Token has expired");
        }

        user.setEnabled(true);
        userRepository.save(user);

        return new CustomResponse(HttpStatus.OK, "Valid");
    }

    @Override
    public ResponseEntity<CustomResponse> resetPassword(String email) throws MessagingException, UnsupportedEncodingException {
        if(email == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "email is required"));
        }
        if(!validateEmail(email)){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "provide correct email format"));
        }
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if(userOpt.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "No account found for this email"));
        }

        UserEntity user = userOpt.get();
        Integer token = theToken();
        emailService.sendResetPasswordEmail(token, user);
        redisTemplate.opsForHash().put(PASSWORD_RESET,email,token);
        redisTemplate.expire(PASSWORD_RESET, 5, TimeUnit.MINUTES);
        return ResponseEntity.ok().body(new CustomResponse(HttpStatus.OK, "Kindly proceed to "+email+" to confirm your password reset"));
    }

    @Override
    public ResponseEntity<?> resendVerificationTokenEmail(String token) throws MessagingException, UnsupportedEncodingException {
        VerificationToken theToken = generateNewVerificationToken(token);
        UserEntity user = theToken.getUser();
        String url = applicationUrl(servletRequest) + "/api/v1/user/verify-email?token=" + theToken.getToken();

        emailService.sendVerificationEmail(url, user);
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK, "New verification link has been sent to your email and it will expire in 1min. Kindly check your email to activate your account"));
    }

    @Override
    public ResponseEntity<CustomResponse> confirmResetPassword(Integer token, ResetPasswordDto request) {
        Integer theToken = (Integer) redisTemplate.opsForHash().get(PASSWORD_RESET, request.getEmail());
        if(theToken != null && theToken.equals(token)){

            Long expirationTime = redisTemplate.getExpire(PASSWORD_RESET, TimeUnit.MINUTES);

            if (expirationTime != null && expirationTime <= 0) {
                return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "Token has expired"));
            }

            if(request.getEmail() == null){
                return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "email is required"));
            }

            if(request.getNewPassword() == null){
                return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "new password is required"));
            }

            Optional<UserEntity> userOpt = userRepository.findByEmail(request.getEmail());
            if(userOpt.isEmpty()){
                return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "No account found for this email"));
            }

            UserEntity user = userOpt.get();

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));

            userRepository.save(user);

            return ResponseEntity.ok(new CustomResponse(HttpStatus.OK, "Your password has been successfully reset. Proceed to login"));
        }else{
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "Invalid token"));
        }    }
    private String applicationUrl(HttpServletRequest servletRequest) {
        return "http://"+servletRequest.getServerName()+":"+servletRequest.getServerPort()+servletRequest.getContextPath();
    }

    public VerificationToken generateNewVerificationToken(String oldToken) {
        Optional<VerificationToken> tokenOpt = tokenRepository.findByToken(oldToken);
        VerificationToken theToken = tokenOpt.get();
        var verificationTokenTime = new VerificationToken();
        theToken.setToken(UUID.randomUUID().toString());
        theToken.setExpirationTime(verificationTokenTime.getTokenExpirationTime());
        return  tokenRepository.save(theToken);
    }

    @Override
    public void saveVerificationToken(UserEntity theUser, String token) {
        var verificationToken = new VerificationToken(token, theUser);
        tokenRepository.save(verificationToken);
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

    public static int theToken() {
        Random random = new Random();
        int min = 100000; // Minimum 6-digit number
        int max = 999999; // Maximum 6-digit number
        return random.nextInt(max - min + 1) + min;
    }
}
