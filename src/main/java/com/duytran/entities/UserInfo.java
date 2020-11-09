package com.duytran.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "userInfo")
@Data
public class UserInfo {
    @Id
    private String id;

    private String fullName;

    private Date dateOfBirth;

    private Gender gender;

    private String phone;

    private String address;

    private String email;

    private TMACredit tmaCredit;

    @Version
    private Long version;

    private Date createdAt;

    private Date updatedAt;

    public UserInfo() {
    }

    public void withdraw (long value) {
        long newBalance = tmaCredit.getBalance() - value;
        tmaCredit.setBalance(newBalance);
    }
    public void deposit (long value) {
        long newBalance = tmaCredit.getBalance() + value;
        tmaCredit.setBalance(newBalance);
    }
}