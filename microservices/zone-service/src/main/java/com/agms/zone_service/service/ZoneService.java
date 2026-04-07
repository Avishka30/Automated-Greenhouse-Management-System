package com.agms.zone_service.service;

import com.agms.zone_service.client.IoTExternalClient;
import com.agms.zone_service.dto.DeviceRegistrationRequest;
import com.agms.zone_service.dto.LoginRequest;
import com.agms.zone_service.dto.LoginResponse;
import com.agms.zone_service.entity.Zone;
import com.agms.zone_service.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ZoneService {

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private IoTExternalClient ioTExternalClient;

    public Zone createZone(Zone zone) {
        // Business Logic 1: Minimum temperature must be strictly less than maximum temperature
        if (zone.getMinTemp() >= zone.getMaxTemp()) {
            throw new IllegalArgumentException("Minimum temperature must be strictly less than maximum temperature.");
        }

        // 1. Get token Login from External API
        LoginResponse loginResponse = ioTExternalClient.login(new LoginRequest("hasindu", "123456"));
        String token = "Bearer " + loginResponse.getAccessToken();

        // 2. Register new device with External API using the obtained token and zone details
//        DeviceRegistrationRequest deviceRequest = new DeviceRegistrationRequest(zone.getName() + "-Sensor", "TEMP-ZONE");
        DeviceRegistrationRequest deviceRequest = new DeviceRegistrationRequest(zone.getName() + "-Sensor", zone.getName());
        Map<String, Object> deviceResponse = ioTExternalClient.registerDevice(token, deviceRequest);

        // 3. Extract the deviceId from the response and set it to the Zone entity
        String externalDeviceId = (String) deviceResponse.get("deviceId");
        zone.setDeviceId(externalDeviceId);

        // 4. Save the Zone entity to the database and return it
        return zoneRepository.save(zone);
    }
}