package com.duytran.thread;

import com.duytran.entities.UserInfo;
import org.apache.log4j.Logger;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class LoanWorker implements Runnable {

    static Logger LOG = Logger.getLogger(LoanWorker.class);

    private final UserInfo userInfo;

    JavaMailSender javaMailSender;

    public LoanWorker(UserInfo userInfo, JavaMailSender javaMailSender) {
        this.userInfo = userInfo;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void run() {
//        try {
//            LOG.info("Thread sleep");
//            Thread.sleep(15000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(userInfo.getEmail());
        mail.setSubject("NOTIFICATION LOAN MONTHLY");
        mail.setText("Your Balance don't enough money to pay for loan");
        LOG.info(String.format("TMA BANKING SYSTEM sent to %s to NOTIFY that your BALANCE don't ENOUGH to pay the loan payment monthly.", userInfo.getEmail()));
        //        javaMailSender.send(mail);
    }
}
