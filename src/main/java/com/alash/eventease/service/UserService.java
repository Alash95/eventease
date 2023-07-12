package com.alash.eventease.service;

import com.alash.eventease.dto.request.*;
import com.alash.eventease.dto.response.CustomResponse;
import com.alash.eventease.dto.response.UserRequestDto;
import com.alash.eventease.model.domain.UserEntity;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface UserService {
    ResponseEntity<CustomResponse> signup(UserRequestDto request);
    ResponseEntity<CustomResponse> signIn(LoginDto request);
    ResponseEntity<CustomResponse> changePassword(ChangePasswordDto request);
    ResponseEntity<CustomResponse> fetchAllUsers();
    ResponseEntity<CustomResponse> fetchSingleUser(FetchUserRequest request);
    void saveVerificationToken(UserEntity theUser, String verificationToken);

    ResponseEntity<CustomResponse> verifyEmail(String token);

    ResponseEntity<?> resendVerificationTokenEmail(String oldToken)throws MessagingException, UnsupportedEncodingException;

    ResponseEntity<CustomResponse> addAddress(Long userId, UserAddressRequest request);

    ResponseEntity<CustomResponse> deleteProfile(Long userId);

    ResponseEntity<CustomResponse> updateProfile(Long userId, UserRequestDto request);

//    ResponseEntity<CustomResponse> changePassword(ChangePasswordDto request);

    ResponseEntity<CustomResponse> uploadProfilePicture(Long userId, MultipartFile file) throws IOException;

    ResponseEntity<CustomResponse> fetchProfilePicture(Long userId);

    ResponseEntity<CustomResponse> resetPassword(String email) throws MessagingException, UnsupportedEncodingException;

    ResponseEntity<CustomResponse> confirmResetPassword(Integer token, ResetPasswordDto request);

}
