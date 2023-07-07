package com.alash.eventease.service;

import com.alash.eventease.dto.request.ChangePasswordDto;
import com.alash.eventease.dto.request.CreateEventRequest;
import com.alash.eventease.dto.request.LoginDto;
import com.alash.eventease.dto.request.RegisterUserRequest;
import com.alash.eventease.dto.response.CustomResponse;
import com.alash.eventease.dto.response.Response;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    Response registerUser(RegisterUserRequest request);

    ResponseEntity<Response> signIn(LoginDto request);
    ResponseEntity<CustomResponse> changePassword(ChangePasswordDto request);
    Response addEvent(CreateEventRequest request);
    List<Response> fetchAllUsers();
    Response fetchSingleUser(String email);


}
