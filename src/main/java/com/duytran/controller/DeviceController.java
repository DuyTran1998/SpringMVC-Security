package com.duytran.controller;

import com.duytran.constant.Header;
import com.duytran.activemq.Sender;
import com.duytran.entities.Device;
import com.duytran.models.MessageTransferModel;
import com.duytran.thread.MessagingWorker;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
    static Logger LOG = Logger.getLogger(DeviceController.class);

    @Autowired
    MessageTransferModel messageTransferModel;

    @Autowired
    Sender sender;

    @Autowired
    ActiveMQConnectionFactory activeMQConnectionFactory;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${device.queueRequest.name}")
    private String queueRequest;

    @Value("${device.queueResponse.name}")
    private String queueResponse;

    // Create a new device.
    @RequestMapping(method = RequestMethod.POST,  produces = "application/json")
    public DeferredResult<ResponseEntity<?>> createDevice(@RequestBody Device device) {
        final DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(10000L);
        deferredResult.onTimeout(() -> {
            deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout!"));
        });
        messageTransferModel.setValue(Header.DEVICE_CREATE, device);;
        new Thread(new MessagingWorker.MessagingWorkerBuilder(deferredResult)
                .setActiveMQConnectionFactory(activeMQConnectionFactory)
                .setObjectMapper(objectMapper)
                .setSender(sender)
                .setMessageTransferModel(messageTransferModel)
                .setQueueRequest(queueRequest)
                .setQueueResponse(queueResponse)
                .build())
                .start();
        return deferredResult;
    }

    // Update a Device
    @RequestMapping(method = RequestMethod.PUT, produces = "application/json")
    public DeferredResult<ResponseEntity<?>> updateDevice(@RequestBody Device device) {
        final DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(10000L);
        deferredResult.onTimeout(() -> {
            deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout!"));
        });

        messageTransferModel.setValue(Header.DEVICE_UPDATE, device);;
        new Thread(new MessagingWorker.MessagingWorkerBuilder(deferredResult)
                .setActiveMQConnectionFactory(activeMQConnectionFactory)
                .setObjectMapper(objectMapper)
                .setSender(sender)
                .setMessageTransferModel(messageTransferModel)
                .setQueueRequest(queueRequest)
                .setQueueResponse(queueResponse)
                .build())
                .start();
        return deferredResult;
    }

    // Delete a device
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public DeferredResult<ResponseEntity<?>> deleteDevice(@PathVariable String id) {
        final DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(10000L);
        deferredResult.onTimeout(() -> {
            deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout!"));
        });

        messageTransferModel.setValue(Header.DEVICE_DELETE, id);
        new Thread(new MessagingWorker.MessagingWorkerBuilder(deferredResult)
                .setActiveMQConnectionFactory(activeMQConnectionFactory)
                .setObjectMapper(objectMapper)
                .setSender(sender)
                .setMessageTransferModel(messageTransferModel)
                .setQueueRequest(queueRequest)
                .setQueueResponse(queueResponse)
                .build())
                .start();
        return deferredResult;
    }

    // Get a device
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public DeferredResult<ResponseEntity<?>> getDevice(@PathVariable String id) {
        final DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(10000L);
        deferredResult.onTimeout(() -> {
            deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout!"));
        });

        messageTransferModel.setValue(Header.DEVICE_GET, id);
        new Thread(new MessagingWorker.MessagingWorkerBuilder(deferredResult)
                .setActiveMQConnectionFactory(activeMQConnectionFactory)
                .setObjectMapper(objectMapper)
                .setSender(sender)
                .setMessageTransferModel(messageTransferModel)
                .setQueueRequest(queueRequest)
                .setQueueResponse(queueResponse)
                .build())
                .start();
        return deferredResult;
    }

    @RequestMapping(method = RequestMethod.GET)
    public DeferredResult<ResponseEntity<?>> getAllDevice() {
        final DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(10000L);
        deferredResult.onTimeout(() -> {
            deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout!"));
        });

        messageTransferModel.setHeader(Header.DEVICE_GET_ALL);
        new Thread(new MessagingWorker.MessagingWorkerBuilder(deferredResult)
                .setActiveMQConnectionFactory(activeMQConnectionFactory)
                .setObjectMapper(objectMapper)
                .setSender(sender)
                .setMessageTransferModel(messageTransferModel)
                .setQueueRequest(queueRequest)
                .setQueueResponse(queueResponse)
                .build())
                .start();
        return deferredResult;
    }
}
