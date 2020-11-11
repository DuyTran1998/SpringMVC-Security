package com.duytran.models;

import lombok.Data;

@Data
public class MessageTransferModel {
    private String header;

    private Object payload;

    public MessageTransferModel() {
    }

    public MessageTransferModel(String header, Object payload) {
        this.header = header;
        this.payload = payload;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setValue(String header, Object payload) {
        this.header = header;
        this.payload = payload;
    }
}