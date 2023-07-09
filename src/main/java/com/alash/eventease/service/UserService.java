package com.alash.eventease.service;

import com.alash.eventease.dto.request.ChangePasswordDto;
import com.alash.eventease.dto.request.FetchUserRequest;
import com.alash.eventease.dto.request.LoginDto;
import com.alash.eventease.dto.response.CustomResponse;
import com.alash.eventease.dto.response.UserRequestDto;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<CustomResponse> signup(UserRequestDto request);
    ResponseEntity<CustomResponse> signIn(LoginDto request);
    ResponseEntity<CustomResponse> changePassword(ChangePasswordDto request);
    ResponseEntity<CustomResponse> fetchAllUsers();
    ResponseEntity<CustomResponse> fetchSingleUser(FetchUserRequest request);

}
