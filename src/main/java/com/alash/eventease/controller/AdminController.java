package com.alash.eventease.controller;

import com.alash.eventease.dto.request.RegisterUserRequest;
import com.alash.eventease.dto.request.UserRoleRequestDto;
import com.alash.eventease.dto.response.ApiResponse;
import com.alash.eventease.dto.response.CustomResponse;
import com.alash.eventease.dto.response.Response;
import com.alash.eventease.service.AdminService;
import com.alash.eventease.service.RoleService;
import com.alash.eventease.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final AdminService adminService;
    private final RoleService roleService;


    @PostMapping("/adminsignup")
    public ResponseEntity<?> registerAdmin(@RequestBody RegisterUserRequest request){
        try {
            var serviceResponse = adminService.registerAdmin(request);
            ApiResponse response = new ApiResponse(true, serviceResponse);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception ex) {
            ApiResponse response = new ApiResponse(false, ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/users")
    public List<Response> getUser(){
        return userService.fetchAllUsers();
    }

    @PostMapping("/create-role")
    public ResponseEntity<CustomResponse> createRole(@RequestBody UserRoleRequestDto request){
        return roleService.createRole(request);
    }

    @PostMapping("/remove-all-users-from-role/{id}")
    public ResponseEntity<CustomResponse> removeUserAllUsersFromRole(@PathVariable("id") Long roleId){
        return roleService.removeAllUserFromRole(roleId);
    }

    @PostMapping("/remove-user-from-role")
    public ResponseEntity<CustomResponse> removeSingleUserFromRole(@RequestParam(name = "userId") Long userId, @RequestParam(name = "roleId")Long roleId){
        return roleService.removeUserFromRole(userId, roleId);
    }

    @PostMapping("/assign-user-to-role")
    public ResponseEntity<CustomResponse> assignUserToRole(@RequestParam(name = "userId") Long userId, @RequestParam(name = "roleId")Long roleId){
        return roleService.assignUserToRole(userId, roleId);
    }

    @DeleteMapping("/delete-role/{id}")
    public ResponseEntity<CustomResponse> deleteRole (@PathVariable("id") Long roleId){
        return roleService.deleteRole(roleId);
    }




}
