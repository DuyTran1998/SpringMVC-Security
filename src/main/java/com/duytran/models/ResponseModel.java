package com.duytran.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseModel {
    private int status;
    private String message;
    private Object object;

    public ResponseModel() {
    }


    public ResponseModel(int status, String message, Object object) {
        this.status = status;
        this.message = message;
        this.object = object;
    }
}
