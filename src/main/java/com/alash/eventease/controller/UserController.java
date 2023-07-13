package com.alash.eventease.controller;

import com.alash.eventease.dto.request.*;
import com.alash.eventease.dto.response.CustomResponse;
import com.alash.eventease.dto.response.UserRequestDto;
import com.alash.eventease.service.BookingService;
import com.alash.eventease.service.EventService;
import com.alash.eventease.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
@Tag(name = "user")
public class UserController {

    private final UserService userService;
    private final EventService eventService;
    private final BookingService bookingService;


    @GetMapping("/verify-email")
    public ResponseEntity<CustomResponse> verifyEmail(@RequestParam("token") String token){
        return userService.verifyEmail(token);
    }



        @PostMapping("/registeruser")
        public ResponseEntity<CustomResponse> registerUser(@RequestBody UserRequestDto request) {
            return userService.signup(request);
        }

    @PostMapping("/signin")
    public ResponseEntity<CustomResponse> signinUser(@RequestBody LoginDto request) {

        return userService.signInUser(request);
    }



    @PostMapping("/reset-password")
    public ResponseEntity<CustomResponse> resetPassword(@RequestParam(name = "email") String email) throws MessagingException, UnsupportedEncodingException {
        return userService.resetPassword(email);
    }

    @GetMapping("/resend-token")
    public ResponseEntity<?> resendVerificationToken(@RequestParam("token") String oldToken) throws MessagingException, UnsupportedEncodingException {
        return userService.resendVerificationTokenEmail(oldToken);
    }

    @PostMapping("/addevent")
    public ResponseEntity<CustomResponse> addEvent(@RequestBody CreateEventRequest request) {
        return eventService.addEvent(request);
    }

    @GetMapping("/fetchallevents")
    public ResponseEntity<CustomResponse> fetchAllEvents() {
        return eventService.fetchAllEvents();
    }

    @GetMapping("/fetchallusers")
    public ResponseEntity<CustomResponse> fetchAllUsers() {
        return userService.fetchAllUsers();
    }

    @GetMapping("/fetchuser/{email}")
    public ResponseEntity<CustomResponse> fetchSingleUserByEmail(@PathVariable("email") String email) {
        return userService.fetchSingleUser(email);
    }

    @PostMapping("/bookevent")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequestDto booking) {
        return bookingService.createBooking(booking);
    }

    @PostMapping("/confirm-password-reset")
    public ResponseEntity<CustomResponse> confirmResetPassword(@RequestParam(name = "token") Integer token, @RequestBody ResetPasswordDto request){
        return userService.confirmResetPassword(token,request);
    }

    @GetMapping("/login/{id}")
    public ResponseEntity<CustomResponse> getRegisteredEvents(@PathVariable(value = "id") Long id){
        return eventService.getRegisteredEvents(id);
    }
}
