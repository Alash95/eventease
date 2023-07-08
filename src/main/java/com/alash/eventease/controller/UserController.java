package com.alash.eventease.controller;

import com.alash.eventease.dto.*;
import com.alash.eventease.service.EventService;
import com.alash.eventease.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
@Tag(name = "user")
public class UserController {

    private final UserService userService;
    private final EventService eventService;




    @PostMapping("/registeruser")
    public ResponseEntity<CustomResponse> registerUser(@RequestBody UserRequestDto request) {
        return userService.signup(request);
    }

    @PostMapping("/signin")
    public ResponseEntity<CustomResponse> signin(@RequestBody LoginDto request) {
        return userService.signIn(request);
    }

    @PutMapping("/passwordreset")
    public ResponseEntity<CustomResponse> resetPassword(@RequestBody ChangePasswordDto request) {
        return userService.changePassword(request);
    }

    @PostMapping("/addevent")
    public ResponseEntity<CustomResponse> addEvent(@RequestBody CreateEventRequest request) {
        return eventService.addEvent(request);
    }

    @GetMapping("/fetchallusers")
    public ResponseEntity<CustomResponse> fetchAllUsers() {
        return userService.fetchAllUsers();
    }

    @GetMapping("/fetchsingleuser")
    public ResponseEntity<CustomResponse> fetchSingleUserByEmail(@RequestBody FetchUserRequest request) {
        return userService.fetchSingleUser(request);
    }
}
