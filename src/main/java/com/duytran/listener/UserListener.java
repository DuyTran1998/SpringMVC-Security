package com.duytran.listener;

import com.duytran.constant.Header;
import com.duytran.entities.TransferOrder;
import com.duytran.entities.UserInfo;
import com.duytran.models.CreditModel;
import com.duytran.models.MessageTransferModel;
import com.duytran.services.UserInfoService;
import com.duytran.services.bankservice.TMACreditService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

@Component
public class UserListener {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TMACreditService tmaCreditService;

    @Autowired
    UserInfoService userInfoService;

    @JmsListener(destination = "userRequestQueue")
    public void receiveEventFromInformationQueue(Message message) throws JMSException, JsonProcessingException {
        String msgBody = ((TextMessage) message).getText();
        String correlationId = message.getJMSCorrelationID();
        Destination replyTo = message.getJMSReplyTo();
        MessageTransferModel messageTransferModel = objectMapper.readValue(msgBody, MessageTransferModel.class);
        String header = messageTransferModel.getHeader();

        switch (header) {
            case Header.USER_UPDATE:
                UserInfo userInfo = objectMapper.convertValue(messageTransferModel.getPayload(), UserInfo.class);
                userInfoService.updateUserInformation(userInfo, correlationId, replyTo);
                break;
            case Header.DEPOSIT_SALARY:
                tmaCreditService.depositSalary();
                break;
            case Header.CHECK_LOAN:
                tmaCreditService.loanPaymentMonthly();
                break;
            case Header.WITHDRAW:
                TransferOrder withdrawTransaction = objectMapper.convertValue(messageTransferModel.getPayload(), TransferOrder.class);
                tmaCreditService.withdraw(withdrawTransaction, correlationId, replyTo);
                break;
            case Header.DEPOSIT:
                TransferOrder depositTransaction = objectMapper.convertValue(messageTransferModel.getPayload(), TransferOrder.class);
                tmaCreditService.deposit(depositTransaction, correlationId);
                break;
            case Header.UPDATE_CREDIT:
                CreditModel creditModel = objectMapper.convertValue(messageTransferModel.getPayload(), CreditModel.class);
                userInfoService.updateCredit(creditModel, correlationId, replyTo);
                break;
        }
    }
}
