package com.alash.eventease.service;

import com.alash.eventease.dto.*;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<CustomResponse> signup(UserRequestDto request);
    ResponseEntity<CustomResponse> signIn(LoginDto request);
    ResponseEntity<CustomResponse> changePassword(ChangePasswordDto request);
    ResponseEntity<CustomResponse> fetchAllUsers();
    ResponseEntity<CustomResponse> fetchSingleUser(FetchUserRequest request);

}
