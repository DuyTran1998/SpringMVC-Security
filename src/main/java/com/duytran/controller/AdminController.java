package com.duytran.controller;

import com.duytran.constant.Header;
import com.duytran.activemq.Sender;
import com.duytran.models.CreditModel;
import com.duytran.models.MessageTransferModel;
import com.duytran.thread.MessagingWorker;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("ROLE_ADMIN")
public class AdminController {

    @Autowired
    MessageTransferModel messageTransferModel;

    @Autowired
    ActiveMQConnectionFactory activeMQConnectionFactory;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Sender sender;

    @Value("${user.queueRequest}")
    private String queueRequest;

    @Value("${user.queueResponse}")
    private String queueResponse;

    @RequestMapping(value = "/user", method = RequestMethod.PUT, produces = "application/json")
    public DeferredResult<ResponseEntity<?>> updateCredit(@RequestBody CreditModel creditModel) {
        final DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(10000L);
        deferredResult.onTimeout(() -> {
            deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout!"));
        });
        messageTransferModel.setValue(Header.UPDATE_CREDIT, creditModel);
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

//    @RequestMapping(value = "/getAllUsers", method = RequestMethod.GET)
//    public ResponseEntity<?> getAllUsers() {
//        return ResponseEntity.ok(userService.getAllUsers());
//    }
//
//
//    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
//    public ResponseEntity<?> deleteUsers(@RequestParam String id) {
//        return ResponseEntity.ok(userService.deleteUser(id));
//    }


}