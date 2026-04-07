package com.agms.zone_service.dto;

public class DeviceRegistrationRequest {
    private String name;
    private String zoneId;

    public DeviceRegistrationRequest(String name, String zoneId) {
        this.name = name;
        this.zoneId = zoneId;
    }
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getZoneId() { return zoneId; }
    public void setZoneId(String zoneId) { this.zoneId = zoneId; }
}