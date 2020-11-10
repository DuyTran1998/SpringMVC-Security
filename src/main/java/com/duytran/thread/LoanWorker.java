package com.duytran.thread;

import com.duytran.entities.UserInfo;
import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class LoanWorker implements Runnable {

    static Logger LOG = Logger.getLogger(LoanWorker.class);

    private final UserInfo userInfo;

    private final MongoTemplate mongoTemplate;

    JavaMailSender javaMailSender;

    public LoanWorker(UserInfo userInfo, MongoTemplate mongoTemplate, JavaMailSender javaMailSender) {
        this.userInfo = userInfo;
        this.mongoTemplate = mongoTemplate;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void run() {
        if (isValidWithdrawAction(userInfo.getTmaCredit().getBalance(), userInfo.getTmaCredit().getLoanPayment())) {
            userInfo.withdraw(userInfo.getTmaCredit().getLoanPayment());
            mongoTemplate.save(userInfo);
            LOG.info(String.format("TMA BANKING SYSTEM withdraw your loan payment month - %s$. %s - balance available: %s$.",
                                                                            userInfo.getTmaCredit().getLoanPayment(),
                                                                            userInfo.getFullName(),
                                                                            userInfo.getTmaCredit().getBalance()));
        }
        else {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(userInfo.getEmail());
            mail.setSubject("NOTIFICATION LOAN MONTHLY");
            mail.setText("Your Balance don't enough money to pay for loan");
            LOG.info(String.format("TMA BANKING SYSTEM sent to %s to NOTIFY that your BALANCE don't ENOUGH " +
                                                            "to pay the loan payment monthly.", userInfo.getEmail()));
        }
        //        javaMailSender.send(mail);
    }

    public boolean isValidWithdrawAction(long balance, long loanValue) {
        return balance - loanValue >= 10;
    }
}
