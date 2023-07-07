package com.alash.eventease.service;

import com.alash.eventease.dto.request.RegisterUserRequest;
import com.alash.eventease.dto.response.Response;

public interface AdminService {

    Response registerAdmin(RegisterUserRequest request);
}
