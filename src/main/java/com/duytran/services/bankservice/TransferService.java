package com.duytran.services.bankservice;

import com.duytran.constant.Header;
import com.duytran.activemq.Sender;
import com.duytran.entities.TransferOrder;
import com.duytran.models.MessageTransferModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("client")
public class TransferService {
    static Logger LOG = Logger.getLogger(TransferService.class);

    @Autowired
    Sender sender;

    @Autowired
    MessageTransferModel messageTransferModel;

    @Value("${user.queueRequest}")
    private String queueRequest;

    public void transferMoney() {
        TransferOrder transaction = new TransferOrder();
        transaction.setValue(500);

        transaction.setSenderID("5fa8a1f1a3ecd4f99d0faf37");
        transaction.setReceiverID("5fa8a1f7a3ecd4f99d0faf39");
        for (int i = 0; i < 3; i++) {
            LOG.info(String.format("The transfer order: from %s to %s with %s.",
                    transaction.getSenderID(),
                    transaction.getReceiverID(),
                    transaction.getValue()));
            final String correlationId = UUID.randomUUID().toString();
            messageTransferModel.setValue(Header.WITHDRAW, transaction);
            sender.sendToQueue(queueRequest, messageTransferModel, correlationId);
        }
    }
}
