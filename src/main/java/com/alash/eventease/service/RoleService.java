package com.alash.eventease.service;

import com.alash.eventease.dto.request.UserRoleRequestDto;
import com.alash.eventease.dto.response.CustomResponse;
import com.alash.eventease.dto.response.Response;

import com.alash.eventease.exception.UserAlreadyExistsException;
import com.alash.eventease.model.UserRole;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RoleService {
    List<UserRole> getAllRoles();

    ResponseEntity<CustomResponse> createRole(UserRoleRequestDto request);

    ResponseEntity<CustomResponse> deleteRole(Long roleId);

    ResponseEntity<CustomResponse> findByName(String name);

    UserRole findById(Long roleId);

    ResponseEntity<CustomResponse> removeUserFromRole(Long userId, Long roleId);

    ResponseEntity<CustomResponse> assignUserToRole(Long userId, Long roleId) throws UserAlreadyExistsException;

    ResponseEntity<CustomResponse> removeAllUserFromRole(Long roleId);
}
