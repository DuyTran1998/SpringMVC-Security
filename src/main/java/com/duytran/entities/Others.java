package com.duytran.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
public class Others {
    private Map<String, Object> ChassisInfo;

    private String SwitchSysObjId;

    private Long SnmpV3AuthenticationProtocol;

    private String SnmpV3UserName;

    private String SnmpV3AuthenticationPassword;

    private String vcSerialNumber;

    private String SnmpStatus;

    private String  SwitchFtpLoginName;

    private String SwitchFtpLoginPasswd;

    private Long tenantId;

    public Others() {

    }
}
