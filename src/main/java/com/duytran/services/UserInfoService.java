package com.duytran.services;

import com.duytran.activemq.Sender;
import com.duytran.config.DateTimeComponent;
import com.duytran.constant.Message;
import com.duytran.entities.*;
import com.duytran.models.CreditModel;
import com.duytran.models.ResponseModel;
import com.duytran.repositories.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.jms.Destination;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class UserInfoService {
    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    Sender sender;

    @Autowired
    DateTimeComponent time;

    @Autowired
    TMACredit tmaCredit;

    @Autowired
    MongoTemplate mongoTemplate;

    public UserInfo insertEmptyUserInformation() {
        UserInfo userInfo = new UserInfo();
        userInfo.setTmaCredit(tmaCredit);
        userInfo.setCreatedAt(time.getDateTimeNow());
        return userInfoRepository.insert(userInfo);
    }

    public void updateUserInformation(UserInfo userInfo, String correlationId, Destination replyTo) {
        if (isInvalidGender(userInfo.getGender())) {
            sender.replyMessage(replyTo, new ResponseModel(HttpStatus.BAD_REQUEST.value(),
                    Message.GENDER_INVALID, userInfo), correlationId);
            return;
        }
        mongoTemplate.updateFirst(query(where("id").is(userInfo.getId())),
                                        new Update().set("fullName", userInfo.getFullName())
                                                    .set("dateOfBirth", userInfo.getDateOfBirth())
                                                    .set("email", userInfo.getEmail())
                                                    .set("gender", userInfo.getGender())
                                                    .set("phone", userInfo.getPhone())
                                                    .set("address", userInfo.getAddress())
                                                    .currentDate("updatedAt"), UserInfo.class);
        sender.replyMessage(replyTo, new ResponseModel(HttpStatus.OK.value(), Message.SUCCESS, userInfo),
                                                                                        correlationId);
    }

    public void updateCredit(CreditModel creditModel, String correlationId, Destination replyTo) {
        if (creditModel.getSalary() < 0) {
            sender.replyMessage(replyTo, new ResponseModel(HttpStatus.BAD_REQUEST.value(),
                    Message.SALARY_INVALID, creditModel), correlationId);
            return;
        }
        if (creditModel.getBonus() < 0) {
            sender.replyMessage(replyTo, new ResponseModel(HttpStatus.BAD_REQUEST.value(),
                    Message.BONUS_INVALID, creditModel), correlationId);
            return;
        }
        if (creditModel.getLoanPayment() < 0) {
            sender.replyMessage(replyTo, new ResponseModel(HttpStatus.BAD_REQUEST.value(),
                    Message.LOAN_INVALID, creditModel), correlationId);
            return;
        }

        mongoTemplate.updateFirst(query(where("id").is(creditModel.getId())),
                                        new Update().set("tmaCredit.salary", creditModel.getSalary())
                                                    .set("tmaCredit.bonus", creditModel.getBonus())
                                                    .set("tmaCredit.loanPayment", creditModel.getLoanPayment())
                                                    .currentDate("updatedAt"), UserInfo.class);
        sender.replyMessage(replyTo, new ResponseModel(HttpStatus.OK.value(), Message.SUCCESS, creditModel),
                                                                                                        correlationId);
    }

    public boolean isInvalidGender(Gender gender) {
        for (Gender genderValue : Gender.values()) {
            if(genderValue.equals(gender)) {
                return false;
            }
        }
        return true;
    }

}
