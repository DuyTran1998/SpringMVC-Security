package com.duytran.constant;

public interface Message {
    String SUCCESS = "Success!";

    String FAIL = "Fail!";

    String NOT_FOUND = "Not found!";

    String USER_EXIST = "User already exists!";

    String OBJECT_NOT_EXIST = "Object doesn't exist!";

    String REQUEST_TIMEOUT = "Request timeout!";

    String GENDER_INVALID = "Gender field is invalid!";

    String SALARY_INVALID = "Salary isn't negative!";

    String BONUS_INVALID = "Bonus isn't negative!";

    String LOAN_INVALID = "Loan isn't negative!";

    String USERNAME_NOT_ALLOW_NULL = "Username is not null!";

    String USERNAME_INVALID = "Username is invalid!";

    String PASSWORD_NOT_ALLOW_NULL = "Password is not null!";

    String ROLE_NOT_ALLOW_NULL = "Role is not null!";

    String RECEIVER_NOT_FOUND = "The receiver is not found in your transfer order.";

    String METHOD_NOT_ALLOWED = "Method is not allowed";

    String LOGIN_FAIL = "Username or password was wrong!";


    // Device message response
    String IPADDRESS_INVALID = "IPAddress is Invalid!";

    String IPADDRESS_EXISTS = "IPAddress already exists";

    String MAC_ADDRESS_INVALID = "MacAddress is Invalid";

    String MAC_ADDRESS_EXISTS = "MacAddress already exists";

    String MANAGED_IP_INVALID = "Managed is Invalid";

    String DISCOVERED_DATETIME_INVALID = "DiscoveredDateTime field is Invalid";

    String LAST_KNOWN_UP_AT_INVALID = "LastKnownUpAt field is Invalid!";

    String MODIFIED_DATE_INVALID = "ModifiedDate field is Invalid!";

    String DATETIME_INVALID = "Datetime field is Invalid!";
}
