package com.duytran.thread;

import com.duytran.entities.UserInfo;
import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class SalaryWorker implements Runnable {

    static Logger LOG = Logger.getLogger(SalaryWorker.class);

    private final MongoTemplate mongoTemplate;

    private final UserInfo userInfo;


    public SalaryWorker(UserInfo userInfo, MongoTemplate mongoTemplate) {
        this.userInfo = userInfo;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run() {
        if (userInfo.getTmaCredit().getBalance() > 0 || userInfo.getTmaCredit().getBonus() > 0) {
            long depositValue = userInfo.getTmaCredit().getSalary() + userInfo.getTmaCredit().getBonus();
            UserInfo newUserInfo = mongoTemplate.findAndModify(query(where("id").is(userInfo.getId())),
                                            new Update().inc("tmaCredit.balance", depositValue),
                                            new FindAndModifyOptions().returnNew(true),UserInfo.class);
            LOG.info(String.format("%s - balance available: %s$ (after deposit salary: %s$ and bonus: %s$)",
                                            newUserInfo.getFullName(),
                                            newUserInfo.getTmaCredit().getBalance(),
                                            newUserInfo.getTmaCredit().getSalary(),
                                            newUserInfo.getTmaCredit().getBonus()));
        }
    }
}
