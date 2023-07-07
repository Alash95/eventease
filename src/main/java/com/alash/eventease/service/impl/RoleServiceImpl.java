package com.alash.eventease.service.impl;

import com.alash.eventease.dto.request.Data;
import com.alash.eventease.dto.request.UserRoleRequestDto;
import com.alash.eventease.dto.response.CustomResponse;
import com.alash.eventease.dto.response.Response;

import com.alash.eventease.exception.RoleAlreadyExistException;
import com.alash.eventease.exception.UserAlreadyExistsException;
import com.alash.eventease.model.User;
import com.alash.eventease.model.UserRole;
import com.alash.eventease.repository.RoleRepository;
import com.alash.eventease.repository.UserRepository;
import com.alash.eventease.service.RoleService;
import com.alash.eventease.utils.ResponseUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    @Override
    public List<UserRole> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public ResponseEntity<CustomResponse> createRole(UserRoleRequestDto request) {
        Optional<UserRole> roleOpt = roleRepository.findByName(request.getName());
        if(roleOpt.isPresent()){
            throw new RoleAlreadyExistException(roleOpt.get().getName()+ " role already exist!");
//            return ResponseEntity.ok(new CustomResponse(HttpStatus.BAD_REQUEST.name(), request, " role already exist!"));

        }
        UserRole role = UserRole.builder()
                .name("ROLE_"+request.getName()).build();
        roleRepository.save(role);
        return ResponseEntity.ok().body(new CustomResponse(HttpStatus.CREATED.name(), request, "Successfully created role"));
    }

    @Override
    public ResponseEntity<CustomResponse> deleteRole(Long roleId) {
        this.removeAllUserFromRole(roleId);
        roleRepository.deleteById(roleId);
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK, "Deleted successfully"));
    }

    @Override
    public ResponseEntity<CustomResponse> findByName(String name) {
        Optional<UserRole> roleOpt =roleRepository.findByName(name);
        if(roleOpt.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "No role found for this name"));
        }

        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), roleOpt.get(), "Successful"));
    }

    @Override
    public UserRole findById(Long roleId) {
        return roleRepository.findById(roleId).get();
    }

    @Override
    public ResponseEntity<CustomResponse> removeUserFromRole(Long userId, Long roleId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<UserRole> role = roleRepository.findById(roleId);
        if(role.isPresent() && role.get().getUsers().contains(user.get())){
            role.get().removeUserFromRole(user.get());
            roleRepository.save(role.get());
            return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), role.get(), "Successfully removed user from role"));
        }
        return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "Failed to remove user from role"));
    }

    @Override
    public ResponseEntity<CustomResponse> assignUserToRole(Long userId, Long roleId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<UserRole> role = roleRepository.findById(roleId);
        if(user.isPresent() && user.get().getRoles().contains(role.get())){
            throw new UserAlreadyExistsException(user.get().getFirstName() + " is already assigned to the " + role.get().getName() + " role");
        }
        role.ifPresent(assignRole -> assignRole.assignUserToRole(user.get()));
        roleRepository.save(role.get());
        Response responseDto = Response.builder()
                .responseCode(ResponseUtils.SUCCESS)
                .responseMessage(ResponseUtils.SUCCESS_MESSAGE)
                .data(Data.builder()
                        .fullName(user.get().getFirstName() + " " + user.get().getLastName())
                        .email(user.get().getEmail())
                        .role(Collections.singleton(role.get()))
                        .build())
                .build();
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), responseDto, "Successfully assigned role to user"));
    }

    @Override
    public ResponseEntity<CustomResponse> removeAllUserFromRole(Long roleId) {
        Optional<UserRole> roleOpt = roleRepository.findById(roleId);
        roleOpt.ifPresent(UserRole ::removeAllUsersFromRole);
        roleRepository.save(roleOpt.get());
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), roleOpt.get(), "Successfully removed all roles from user"));
    }
}
