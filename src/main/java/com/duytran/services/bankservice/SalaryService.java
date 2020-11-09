package com.duytran.services.bankservice;

import com.duytran.constant.Header;
import com.duytran.activemq.Sender;
import com.duytran.models.MessageTransferModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("accountant")
public class SalaryService {
    @Autowired
    Sender sender;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    MessageTransferModel messageTransferModel;

    @Value("${user.queueRequest}")
    private String queueRequest;

    public void depositSalary() {
        final String correlationId = UUID.randomUUID().toString();
        messageTransferModel.setHeader(Header.DEPOSIT_SALARY);
        sender.sendToQueue(queueRequest, messageTransferModel, correlationId);
    }
}
