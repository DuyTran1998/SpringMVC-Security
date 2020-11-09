package com.duytran.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditModel {
    private String id;
    private long loanPayment = 0;
    private long salary = 0;
    private long bonus = 0;

    public CreditModel(String id, long loanPayment, long salary, long bonus) {
        this.id = id;
        this.loanPayment = loanPayment;
        this.salary = salary;
        this.bonus = bonus;
    }

    public CreditModel() {

    }
}
