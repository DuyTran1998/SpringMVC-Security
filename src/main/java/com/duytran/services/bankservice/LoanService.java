package com.duytran.services.bankservice;

import com.duytran.constant.Header;
import com.duytran.activemq.Sender;
import com.duytran.models.MessageTransferModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("loan")
public class LoanService {
    @Autowired
    Sender sender;

    @Autowired
    MessageTransferModel messageTransferModel;

    @Value("${user.queueRequest}")
    private String queueRequest;

    public void checkLoanPaymentMonthly() {
        final String correlationId = UUID.randomUUID().toString();
        messageTransferModel.setHeader(Header.CHECK_LOAN);
        sender.sendToQueue(queueRequest, messageTransferModel, correlationId);
    }
}
