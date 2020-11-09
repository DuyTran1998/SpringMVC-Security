package com.duytran.controller;

import com.duytran.constant.Header;
import com.duytran.activemq.Sender;
import com.duytran.constant.Message;
import com.duytran.entities.TransferOrder;
import com.duytran.entities.UserInfo;
import com.duytran.models.MessageTransferModel;
import com.duytran.models.ResponseModel;
import com.duytran.repositories.UserInfoRepository;
import com.duytran.services.UserService;
import com.duytran.thread.MessagingWorker;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    MessageTransferModel messageTransferModel;

    @Autowired
    Sender sender;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ActiveMQConnectionFactory activeMQConnectionFactory;

    @Autowired
    UserService userService;

    @Autowired
    UserInfoRepository userInfoRepository;

    @Value("${user.queueRequest}")
    private String queueRequest;

    @Value("${user.queueResponse}")
    private String queueResponse;


    @RequestMapping(method = RequestMethod.PUT, produces = "application/json")
    public DeferredResult<ResponseEntity<?>> updateUserInfo(@RequestBody UserInfo userInfo) {
        final DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(10000L);
        deferredResult.onTimeout(() -> {
            deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body(Message.REQUEST_TIMEOUT));
        });
        if (userInfoRepository.checkExistById(userInfo.getId())) {
            if (userService.getUserNameLogged().equals(userService.getUserNameByUserInfoId(userInfo.getId()))) {
                messageTransferModel.setValue(Header.USER_UPDATE, userInfo);;
                new Thread( new MessagingWorker.MessagingWorkerBuilder(deferredResult)
                        .setActiveMQConnectionFactory(activeMQConnectionFactory)
                        .setSender(sender)
                        .setObjectMapper(objectMapper)
                        .setMessageTransferModel(messageTransferModel)
                        .setQueueRequest(queueRequest)
                        .setQueueResponse(queueResponse)
                        .build())
                        .start();
                return deferredResult;
            }
            deferredResult.setResult(ResponseEntity.ok(new ResponseModel(HttpStatus.METHOD_NOT_ALLOWED.value(),
                    Message.METHOD_NOT_ALLOWED, userInfo)));
            return deferredResult;
        }
        deferredResult.setResult(ResponseEntity.ok(new ResponseModel(HttpStatus.METHOD_NOT_ALLOWED.value(),
                Message.OBJECT_NOT_EXIST, userInfo)));
        return deferredResult;
    }

    @RequestMapping(value = "/transfer", method = RequestMethod.POST, produces= "application/json")
    public DeferredResult<ResponseEntity<?>> transferMoney(@RequestBody TransferOrder transferOrder) {
        final DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(20000L);
        deferredResult.onTimeout(() -> {
            deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout!"));
        });
        if (userService.getUserNameLogged().equals(userService.getUserNameByUserInfoId(transferOrder.getSenderID()))) {
            if (userInfoRepository.checkExistById(transferOrder.getReceiverID())) {
                messageTransferModel.setValue(Header.WITHDRAW, transferOrder);;
                new Thread( new MessagingWorker.MessagingWorkerBuilder(deferredResult)
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
            deferredResult.setResult(ResponseEntity.ok(new ResponseModel(HttpStatus.NOT_FOUND.value(),
                    Message.RECEIVER_NOT_FOUND, transferOrder)));
            return deferredResult;
        }
        deferredResult.setResult(ResponseEntity.ok(new ResponseModel(HttpStatus.METHOD_NOT_ALLOWED.value(),
                Message.METHOD_NOT_ALLOWED, transferOrder)));
        return deferredResult;
    }
}
