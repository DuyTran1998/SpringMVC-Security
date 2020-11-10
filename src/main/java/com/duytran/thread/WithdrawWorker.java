package com.duytran.thread;

import com.duytran.activemq.Sender;
import com.duytran.constant.Header;
import com.duytran.entities.StatusTransaction;
import com.duytran.entities.TransferOrder;
import com.duytran.entities.UserInfo;
import com.duytran.models.MessageTransferModel;
import com.duytran.repositories.TransactionRepository;
import org.apache.log4j.Logger;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.jms.Destination;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class WithdrawWorker implements Runnable{
    static Logger LOG = Logger.getLogger(WithdrawWorker.class);
    private final TransferOrder transferOrder;
    private final TransactionRepository transRepository;
    private final MongoTemplate mongoTemplate;
    private final Sender sender;
    private final MessageTransferModel messageTransferModel;
    private final Destination destination;
    private final String correlationId;

    public WithdrawWorker(WithdrawWorkerBuilder builder) {
        this.transferOrder = builder.transferOrder;
        this.transRepository = builder.transRepository;
        this.mongoTemplate = builder.mongoTemplate;
        this.sender = builder.sender;
        this.messageTransferModel = builder.messageTransferModel;
        this.destination = builder.destination;
        this.correlationId = builder.correlationId;
    }

    @Override
    public void run() {
        UserInfo userInfo = mongoTemplate.findOne(query(where("id").is(transferOrder.getSenderID())), UserInfo.class);
        if (isValidWithdrawAction(userInfo.getTmaCredit().getBalance(), transferOrder.getValue())) {
            try {
                userInfo.withdraw(transferOrder.getValue());
                mongoTemplate.save(userInfo);
                LOG.info(String.format("%s - balance available: %s$ (after Withdraw: %s$ sent to %s)",
                                            userInfo.getFullName(),
                                            userInfo.getTmaCredit().getBalance(),
                                            transferOrder.getValue(),
                                            transferOrder.getReceiverID()));

                transferOrder.setStatus(StatusTransaction.ON_WAITING.toString());
                TransferOrder savedTrans = transRepository.insert(transferOrder);

                messageTransferModel.setValue(Header.DEPOSIT, savedTrans);
                sender.sendToQueue("userRequestQueue", messageTransferModel, correlationId);

                LOG.info(Header.DEPOSIT +  " " + transferOrder.getReceiverID());
//                sender.replyMessage(destination, new ResponseModel(HttpStatus.OK.value(),
//                        MessageResponse.TRANSACTION_SUCCESS, transaction), correlationId);
            } catch ( OptimisticLockingFailureException e) {
                LOG.info("Re-Withdraw");
                messageTransferModel.setValue(Header.WITHDRAW, transferOrder);
                sender.sendToQueue("userRequestQueue", messageTransferModel, correlationId);
            }
        } else {
//            sender.replyMessage(destination, new ResponseModel(HttpStatus.BAD_REQUEST.value(),
//                    MessageResponse.NOT_ENOUGH_MONEY, transaction.getId()), correlationId);
            LOG.info(String.format("%s don\"t ENOUGH MONEY to release the transfer order.", userInfo.getFullName()));
        }
    }

    public boolean isValidWithdrawAction(long balance, long withdrawValue) {
        return balance - withdrawValue >= 10;
    }

    public static class WithdrawWorkerBuilder{
        private final TransferOrder transferOrder;
        private TransactionRepository transRepository;
        private MongoTemplate mongoTemplate;
        private Sender sender;
        private MessageTransferModel messageTransferModel;
        private Destination destination;
        private String correlationId;

        public WithdrawWorkerBuilder(TransferOrder transferOrder) {
            this.transferOrder = transferOrder;
        }


        public WithdrawWorker.WithdrawWorkerBuilder setTransactionRepository(TransactionRepository transRepository) {
            this.transRepository = transRepository;
            return this;
        }

        public WithdrawWorker.WithdrawWorkerBuilder setMongoTemplate(MongoTemplate mongoTemplate) {
            this.mongoTemplate = mongoTemplate;
            return this;
        }

        public WithdrawWorker.WithdrawWorkerBuilder setSender(Sender sender) {
            this.sender = sender;
            return this;
        }

        public WithdrawWorker.WithdrawWorkerBuilder setDestination(Destination destination) {
            this.destination = destination;
            return this;
        }

        public WithdrawWorker.WithdrawWorkerBuilder setCorrelationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public WithdrawWorker.WithdrawWorkerBuilder setMessageTransferObject(MessageTransferModel messageTransferModel) {
            this.messageTransferModel = messageTransferModel;
            return this;
        }

        public WithdrawWorker build() {
            return new WithdrawWorker(this);
        }
    }
}
