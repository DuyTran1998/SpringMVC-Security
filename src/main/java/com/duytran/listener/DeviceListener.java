package com.duytran.listener;

import com.duytran.constant.Header;
import com.duytran.entities.Device;
import com.duytran.models.MessageTransferModel;
import com.duytran.services.device.DeviceService;
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
public class DeviceListener {

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  DeviceService deviceService;

  @JmsListener(destination = "devicesRequestQueue")
  public void receiveEventFromDevicesQueue(Message message) throws JsonProcessingException, JMSException, InterruptedException {
        String msgBody = ((TextMessage) message).getText();
        String correlationId = message.getJMSCorrelationID();
        Destination replyTo = message.getJMSReplyTo();
        MessageTransferModel messageTransferModel = objectMapper.readValue(msgBody, MessageTransferModel.class);

        String header = messageTransferModel.getHeader();
        switch (header) {
            case Header.DEVICE_CREATE:
                Device device = objectMapper.convertValue(messageTransferModel.getPayload(), Device.class);
                deviceService.createDevice(device, correlationId, replyTo);
                 break;
            case Header.DEVICE_UPDATE:
                Device update = objectMapper.convertValue(messageTransferModel.getPayload(), Device.class);
                deviceService.updateDevice(update, correlationId, replyTo);
                break;
            case Header.DEVICE_DELETE:
                String idToDelete = objectMapper.convertValue(messageTransferModel.getPayload(), String.class);
                deviceService.deleteDevice(idToDelete, correlationId, replyTo);
                break;
            case Header.DEVICE_GET:
                String idToGet = objectMapper.convertValue(messageTransferModel.getPayload(), String.class);
                deviceService.getDevice(idToGet, correlationId, replyTo);
                break;
    }
  }
}

