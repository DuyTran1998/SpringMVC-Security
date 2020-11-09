package com.duytran.thread;

import com.duytran.entities.StatusTransaction;
import com.duytran.entities.TransferOrder;
import com.duytran.entities.UserInfo;
import org.apache.log4j.Logger;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class DepositWorker implements Runnable {
    static Logger LOG = Logger.getLogger(DepositWorker.class);
    private final TransferOrder transferOrder;
    private final MongoTemplate mongoTemplate;

    public DepositWorker(DepositWorkerBuilder builder) {
        this.transferOrder = builder.transferOrder;
        this.mongoTemplate = builder.mongoTemplate;
    }

    @Override
    public void run() {
        if (transferOrder.getValue() >= 0) {
            try {
                UserInfo newUserInfo = mongoTemplate.findAndModify(query(where("id").is(transferOrder.getReceiverID())),
                                                    new Update().inc("tmaCredit.balance", transferOrder.getValue()),
                                                    new FindAndModifyOptions().returnNew(true),UserInfo.class);
                mongoTemplate.updateFirst(query(where("id").is(transferOrder.getId())), Update.update("status",
                                                    StatusTransaction.SUCCESS.toString()), TransferOrder.class);
                LOG.info(String.format("%s - balance available: %s$ (after Deposit: %s$ from %s)",
                                                    newUserInfo.getFullName(),
                                                    newUserInfo.getTmaCredit().getBalance(),
                                                    transferOrder.getValue(),
                                                    transferOrder.getSenderID()));
            } catch (OptimisticLockingFailureException e) {
                LOG.info("re-deposit");
//               messageTransferModel.setValue(Header.DEPOSIT, transaction);
//               sender.sendToQueue("userRequestQueue", messageTransferModel, correlationId);
            }
        } else {
            mongoTemplate.updateFirst(query(where("id").is(transferOrder.getId())),
                    new Update().set("status", StatusTransaction.FAIL.toString()),
                    transferOrder.getClass());
        }
    }

    public static class DepositWorkerBuilder{
        private final TransferOrder transferOrder;
        private MongoTemplate mongoTemplate;

        public DepositWorkerBuilder(TransferOrder transferOrder) {
            this.transferOrder = transferOrder;
        }

        public DepositWorker.DepositWorkerBuilder setMongoTemplate(MongoTemplate mongoTemplate) {
            this.mongoTemplate = mongoTemplate;
            return this;
        }

        public DepositWorker build() {
            return new DepositWorker(this);
        }
    }
}
