package com.alash.eventease.service.impl;

import com.alash.eventease.dto.request.Data;
import com.alash.eventease.dto.request.RegisterUserRequest;
import com.alash.eventease.dto.response.Response;
import com.alash.eventease.model.User;
import com.alash.eventease.model.UserRole;
import com.alash.eventease.repository.RoleRepository;
import com.alash.eventease.repository.UserRepository;
import com.alash.eventease.service.AdminService;
import com.alash.eventease.utils.ResponseUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
@Service
public class AdminServiceImpl implements AdminService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public AdminServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public Response registerAdmin(RegisterUserRequest request) {
        boolean isEmailExists = userRepository.existsByEmail(request.getEmail());

        if (isEmailExists) {
            return Response.builder()
                    .responseCode(ResponseUtils.USER_EXISTS_CODE)
                    .responseMessage(ResponseUtils.USER_EXISTS_MESSAGE)
                    .data(null)
                    .build();
        }

        UserRole role = roleRepository.findByName("ROLE_ADMIN").get();
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
}
