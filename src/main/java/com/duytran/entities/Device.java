package com.duytran.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Map;

@Document(collection = "devices")
@Setter
@Getter
public class Device {
    @Id
    private String id;

    private String name;

    private String description;

    private Date discoveredDateTime;

    private String ipAddress;

    private Date lastKnownUpAt;

    private String location;

    private String macAddress;

    private String runningFrom;

    private String managedIp;

    private String seenBy;

    private String version;

    private String status;

    private String licenseType;

    private Map<String, Object> others;

    private Date modifiedDate;
}
