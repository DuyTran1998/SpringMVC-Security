package com.duytran.activemq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.*;


@Component
public class Sender {

  @Autowired
  private JmsTemplate jmsTemplate;

  public void sendToQueue(String destination, Object object, String correlationId) {
    jmsTemplate.convertAndSend(destination, object, m -> {
      m.setJMSCorrelationID(correlationId);
      return m;
    });
  }

  public void replyMessage(Destination destination, Object object, String correlationId) {
    jmsTemplate.convertAndSend(destination, object, m -> {
      m.setJMSCorrelationID(correlationId);
      return m;
    });
  }

  public void sendMessage(Destination destination, Object object, String correlationId, Destination replyTo) {
    jmsTemplate.convertAndSend(destination, object, m -> {
      m.setJMSCorrelationID(correlationId);
      m.setJMSReplyTo(replyTo);
      return m;
    });
  }
}
