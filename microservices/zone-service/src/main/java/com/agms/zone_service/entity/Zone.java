package com.agms.zone_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "zones") // Database table name
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double minTemp;

    private Double maxTemp;

    // Save the device ID as a string (could be a UUID or any unique identifier)
    private String deviceId;

    // --- Constructors ---
    public Zone() {}

    public Zone(String name, Double minTemp, Double maxTemp, String deviceId) {
        this.name = name;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.deviceId = deviceId;
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getMinTemp() { return minTemp; }
    public void setMinTemp(Double minTemp) { this.minTemp = minTemp; }

    public Double getMaxTemp() { return maxTemp; }
    public void setMaxTemp(Double maxTemp) { this.maxTemp = maxTemp; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
}