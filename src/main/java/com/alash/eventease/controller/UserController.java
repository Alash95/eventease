package com.alash.eventease.controller;

import com.alash.eventease.dto.request.ChangePasswordDto;
import com.alash.eventease.dto.request.CreateEventRequest;
import com.alash.eventease.dto.request.LoginDto;
import com.alash.eventease.dto.request.RegisterUserRequest;
import com.alash.eventease.dto.response.ApiResponse;
import com.alash.eventease.dto.response.CustomResponse;
import com.alash.eventease.dto.response.Response;
import com.alash.eventease.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/fetchAll")
    public List<Response> getAllUsers() {
        return userService.fetchAllUsers();
    }
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserRequest request){
        try {
            var serviceResponse = userService.registerUser(request);
            ApiResponse response = new ApiResponse(true, serviceResponse);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception ex) {
            ApiResponse response = new ApiResponse(false, ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }




    @PostMapping("/signin")
    public ResponseEntity<Response> loginUsers(@RequestBody LoginDto request){
        return userService.signIn(request);
    }

    @PostMapping("/resetpassword")
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CustomResponse> resetPassword(@RequestBody ChangePasswordDto request){
        return userService.changePassword(request);
    }

    @PostMapping("/event")
    public ResponseEntity<?> addEvent(@RequestBody CreateEventRequest request){
        try {
            var serviceResponse = userService.addEvent(request);
            ApiResponse response = new ApiResponse(true, serviceResponse);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception ex) {
            ApiResponse response = new ApiResponse(false, ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getUser")
    public ResponseEntity<Response> getUserByEmail(@RequestBody String email){
        return ResponseEntity.ok((userService.fetchSingleUser(email)));
    }
}
