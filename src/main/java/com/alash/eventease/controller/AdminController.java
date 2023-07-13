package com.alash.eventease.controller;

import com.alash.eventease.dto.request.LoginDto;
import com.alash.eventease.dto.response.CustomResponse;
import com.alash.eventease.dto.response.UserRequestDto;
import com.alash.eventease.dto.response.UserResponseDto;
import com.alash.eventease.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "admin")
public class AdminController {
    private final UserService userService;

    @Operation(
            summary = "fetch all users",
            description = "This endpoint fetches all users from database",
            responses = {
                    @ApiResponse(responseCode = "200",
                    description = "Success",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))
                    ),

                    @ApiResponse(responseCode = "204",
                            description = "NO content",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class))
                    ),
                    @ApiResponse(responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class))
                    )
            }
    )
    @GetMapping("/users")
    public ResponseEntity<CustomResponse> getUser(){
        return userService.fetchAllUsers();
    }

    @PostMapping("/registeradmin")
    public ResponseEntity<CustomResponse> registerAdmin(@RequestBody UserRequestDto request) {
        return userService.signupAdmin(request);
    }

    @PostMapping("/signin")
    public ResponseEntity<CustomResponse> signinAdmin(@RequestBody LoginDto request) {
        return userService.signInAdmin(request);
    }

}
