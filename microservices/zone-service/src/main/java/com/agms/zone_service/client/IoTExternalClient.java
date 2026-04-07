package com.agms.zone_service.client;

import com.agms.zone_service.dto.DeviceRegistrationRequest;
import com.agms.zone_service.dto.LoginRequest;
import com.agms.zone_service.dto.LoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

// External IoT API Base URL එක [cite: 47]
@FeignClient(name = "iot-external-service", url = "http://104.211.95.241:8080/api")
public interface IoTExternalClient {

    // 1. Get token Login [cite: 60-65]
    @PostMapping("/auth/login")
    LoginResponse login(@RequestBody LoginRequest loginRequest);

    // 2. Register new device [cite: 75-81]
    @PostMapping("/devices")
    Map<String, Object> registerDevice(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody DeviceRegistrationRequest deviceRequest
    );
}