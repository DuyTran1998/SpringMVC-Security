package com.duytran.thread;

import com.duytran.activemq.Sender;
import com.duytran.models.MessageTransferModel;
import com.duytran.models.ResponseModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import javax.jms.*;
import java.util.UUID;

public class MessagingWorker implements Runnable {

    private final DeferredResult<ResponseEntity<?>> deferredResult;

    private final ObjectMapper objectMapper;

    private final ActiveMQConnectionFactory activeMQConnectionFactory;

    private final MessageTransferModel msg;

    private final String queueRequest;

    private final String queueResponse;

    private final Sender sender;

    private MessageConsumer replyConsumer;

    public MessagingWorker(MessagingWorkerBuilder builder) {
        this.deferredResult = builder.deferredResult;
        this.msg = builder.messageTransferModel;
        this.sender = builder.sender;
        this.activeMQConnectionFactory = builder.activeMQConnectionFactory;
        this.objectMapper = builder.objectMapper;
        this.queueRequest = builder.queueRequest;
        this.queueResponse = builder.queueResponse;
    }

    @Override
    public void run() {
        final String correlationId = UUID.randomUUID().toString();
        try {
            String selector = String.format("JMSCorrelationID='%s'", correlationId);
            Connection reqQueueConnection = activeMQConnectionFactory.createConnection();
            Session sessionRequest = reqQueueConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination requestQueue = sessionRequest.createQueue(queueRequest);
            Queue replyTo = sessionRequest.createQueue(queueResponse);
            // Send message to queue
            sender.sendMessage(requestQueue, msg, correlationId, replyTo);
            reqQueueConnection.close();

            QueueConnection resQueueConnection = activeMQConnectionFactory.createQueueConnection();
            resQueueConnection.start();
            QueueSession queueSession = resQueueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            replyConsumer = queueSession.createReceiver(replyTo, selector);

            // Wait response from queue
            receiveAsync();
            replyConsumer.close();
            queueSession.close();
            resQueueConnection.close();
        } catch (JMSException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void receiveAsync() throws JMSException, JsonProcessingException {
        // Wait with timeout 5s
        Message msg = replyConsumer.receive(5000);
        if (msg != null) {
            String msgBody = ((TextMessage) msg).getText();
            ResponseModel responseModel = objectMapper.readValue(msgBody, ResponseModel.class);
            deferredResult.setResult(ResponseEntity.ok(responseModel));
        }
        deferredResult.setErrorResult("Request Timeout");
    }

    public static class MessagingWorkerBuilder {
        private final DeferredResult<ResponseEntity<?>> deferredResult;
        private ActiveMQConnectionFactory activeMQConnectionFactory;
        private Sender sender;
        private MessageTransferModel messageTransferModel;
        private ObjectMapper objectMapper;
        private String queueRequest;
        private String queueResponse;

        public MessagingWorkerBuilder(DeferredResult<ResponseEntity<?>> deferredResult) {
            this.deferredResult = deferredResult;
        }

        public MessagingWorkerBuilder setActiveMQConnectionFactory(ActiveMQConnectionFactory activeMQConnectionFactory) {
            this.activeMQConnectionFactory = activeMQConnectionFactory;
            return this;
        }

        public MessagingWorkerBuilder setSender(Sender sender) {
            this.sender = sender;
            return this;
        }

        public MessagingWorkerBuilder setMessageTransferModel(MessageTransferModel messageTransferModel) {
            this.messageTransferModel = messageTransferModel;
            return this;
        }

        public MessagingWorkerBuilder setObjectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        public MessagingWorkerBuilder setQueueRequest(String queueRequest) {
            this.queueRequest = queueRequest;
            return this;
        }

        public MessagingWorkerBuilder setQueueResponse(String queueResponse) {
            this.queueResponse = queueResponse;
            return this;
        }

        public MessagingWorker build() {
            return new MessagingWorker(this);
        }
    }
}
