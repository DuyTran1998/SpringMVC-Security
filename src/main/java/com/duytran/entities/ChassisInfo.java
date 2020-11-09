package com.duytran.entities;

import lombok.Data;

@Data
public class ChassisInfo {
    private String chassisPartNumber;

    private String chassisModuleType;

    private String chassisRole;

    private String modelName;

    private Long chassisId;

    private String chassisManufactureDate;

    private String chassisAdminStatus;

    private String chassisSlot;

    private Long chassisPhysicalIndex;

    private String chassisOperationalStatus;

    private String chassisDescription;

    private String chassisHardwareRevision;

    private String chassisSerialNumber;

    private String chassisMacAddress;

    private String chassisNumberOfResets;

    public ChassisInfo() {
    }
}
