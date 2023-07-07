package com.alash.eventease.service.impl;

import com.alash.eventease.dto.request.*;
import com.alash.eventease.dto.response.CustomResponse;
import com.alash.eventease.dto.response.Response;
import com.alash.eventease.model.User;
import com.alash.eventease.model.UserRole;
import com.alash.eventease.repository.RoleRepository;
import com.alash.eventease.repository.UserRepository;
import com.alash.eventease.service.EventService;
import com.alash.eventease.service.UserService;
import com.alash.eventease.utils.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EventService eventService;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserRepository userRepository, EventService eventService, RoleRepository roleRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.eventService = eventService;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Response registerUser(RegisterUserRequest request) {
        boolean isEmailExists = userRepository.existsByEmail(request.getEmail());

        if (isEmailExists) {
            return Response.builder()
                    .responseCode(ResponseUtils.USER_EXISTS_CODE)
                    .responseMessage(ResponseUtils.USER_EXISTS_MESSAGE)
                    .data(null)
                    .build();
        }

        UserRole role = roleRepository.findByName("ROLE_USER").get();
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .roles((Collections.singleton(role)))
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        return Response.builder()
                .responseCode(ResponseUtils.SUCCESS)
                .responseMessage(ResponseUtils.USER_REGISTERED_SUCCESS)
                .data(Data.builder()
                        .email(savedUser.getEmail())
                        .fullName(savedUser.getFirstName() + " " + savedUser.getLastName())
                        .role(new HashSet<>(user.getRoles()))
                        .build())
                .build();
    }



    @Override
    public ResponseEntity<Response> signIn(LoginDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new  ResponseEntity<>(
                Response.builder()
                        .responseCode(ResponseUtils.SUCCESSFUL_LOGIN_RESPONSE_CODE)
                        .responseMessage(ResponseUtils.SUCCESSFUL_LOGIN_MESSAGE)
                        .data(null)
                        .build(), HttpStatus.CREATED);    }

    @Override
    public ResponseEntity<CustomResponse> changePassword(ChangePasswordDto request) {
        if(request.getEmail() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "email is required"));
        }
//        if(!validateEmail(request.getEmail())){
//            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "provide correct email format"));
//        }
        if(request.getOldPassword() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "oldPassword is required"));
        }
        if(request.getNewPassword() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "newPassword is required"));
        }
        Optional<User> userOpt = userRepository.findUserByEmail(request.getEmail());

        if(userOpt.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "No user is associated with this email"));
        }

        User user = userOpt.get();
        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "Old password is not correct. Try again"));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK, "Password has been successfully changed"));
    }


    @Override
    public Response addEvent(CreateEventRequest request) {
        User foundUser = userRepository.findUserByEmail(request.getEmail()).get();
        if (!userRepository.existsByEmail(request.getEmail())){
            return Response.builder()
                    .responseCode(ResponseUtils.USER_NOT_FOUND_CODE)
                    .responseMessage(ResponseUtils.USER_NOT_FOUND)
                    .data(null)
                    .build();
        }
        EventDto event = new EventDto();
        event.setEmail(request.getEmail());
        event.setEventName(request.getEventName());
        event.setLocation(request.getEventLocation());
        event.setDescription(request.getDescription());

        eventService.saveEvent(event);


        return Response.builder()
                .responseCode(ResponseUtils.SUCCESS)
                .responseMessage(ResponseUtils.SUCCESS_MESSAGE)
                .data(Data.builder()
                        .email(foundUser.getEmail())
                        .eventName(request.getEventName())
                        .eventLocation(request.getEventLocation())
                        .build())
                .build();
    }

    @Override
    public List<Response> fetchAllUsers() {
        List<User> userList =userRepository.findAll();

        List<Response> response = new ArrayList<>();
        for (User user: userList) {
            response.add(Response.builder()
                            .responseCode(ResponseUtils.SUCCESS)
                            .responseMessage(ResponseUtils.SUCCESS_MESSAGE)
                            .data(Data.builder()
                                    .fullName(user.getFirstName() + " " + user.getLastName())
                                    .email(user.getEmail())
                                    .role(new HashSet<>(user.getRoles()))
                                    .build())
                    .build());
        }
        return response;
    }

    @Override
    public Response fetchSingleUser(String email) {
        if (!userRepository.existsByEmail(email)) {
            return Response.builder()
                    .responseCode(ResponseUtils.USER_NOT_FOUND_CODE)
                    .responseMessage(ResponseUtils.USER_NOT_FOUND)
                    .data(null)
                    .build();
        }
        User user = userRepository.findUserByEmail(email).get();

        return Response.builder()
                .responseCode(ResponseUtils.SUCCESS)
                .responseMessage(ResponseUtils.SUCCESS_MESSAGE)
                .data(Data.builder()
                        .fullName(user.getFirstName() + " " + user.getLastName())
                        .email(user.getEmail())
                        .build())
                .build();
    }


}
