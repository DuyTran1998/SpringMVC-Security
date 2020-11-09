package com.duytran.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Version;

@Getter
@Setter
public class TMACredit {
    private long balance = 0;
    private long loanPayment = 0;
    private long salary = 0;
    private long bonus = 0;

    public TMACredit(long balance, long loanPayment, long salary, long bonus) {
        this.balance = balance;
        this.loanPayment = loanPayment;
        this.salary = salary;
        this.bonus = bonus;
    }

    public TMACredit() {
    }

}
