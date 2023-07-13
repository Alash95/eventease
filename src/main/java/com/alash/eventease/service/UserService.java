package com.alash.eventease.service;

import com.alash.eventease.dto.request.*;
import com.alash.eventease.dto.response.CustomResponse;
import com.alash.eventease.dto.response.UserRequestDto;
import com.alash.eventease.model.domain.UserEntity;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;

public interface UserService {
    ResponseEntity<CustomResponse> signup(UserRequestDto request);
    ResponseEntity<CustomResponse> signupAdmin(UserRequestDto request);
    ResponseEntity<CustomResponse> signInUser(LoginDto request);
    ResponseEntity<CustomResponse> loginUser(LoginDto request);
    ResponseEntity<CustomResponse> signInAdmin(LoginDto request);
    ResponseEntity<CustomResponse> changePassword(ChangePasswordDto request);
    ResponseEntity<CustomResponse> fetchAllUsers();
    ResponseEntity<CustomResponse> fetchSingleUser(String email);
    ResponseEntity<CustomResponse> resetPassword(String email) throws MessagingException, UnsupportedEncodingException;
    ResponseEntity<CustomResponse> confirmResetPassword(Integer token, ResetPasswordDto request);

    CustomResponse validateToken(String token);
    ResponseEntity<CustomResponse> fetchUserById(Long userId);

    void saveVerificationToken(UserEntity theUser, String verificationToken);

    ResponseEntity<CustomResponse> verifyEmail(String token);

    ResponseEntity<?> resendVerificationTokenEmail(String token)throws MessagingException, UnsupportedEncodingException;



}
