package com.duytran.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Document(collection = "transferHistory")
@Getter
@Setter
public class TransferOrder {
    @Id
    private String id;

    private String senderID;

    private String receiverID;

    private long value;

    private Date createAt;

    private Date modifiedDate;

    private String status;

    public TransferOrder() {
    }

    public TransferOrder(String id, String senderID, String receiverID, long value, Date createAt, String status) {
        this.id = id;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.value = value;
        this.status = status;
        this.createAt = createAt;
    }
}
