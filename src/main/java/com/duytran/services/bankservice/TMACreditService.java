package com.duytran.services.bankservice;

import com.duytran.activemq.Sender;
import com.duytran.entities.TransferOrder;
import com.duytran.entities.UserInfo;
import com.duytran.models.MessageTransferModel;
import com.duytran.repositories.TransactionRepository;
import com.duytran.repositories.UserInfoRepository;
import com.duytran.thread.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.jms.Destination;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TMACreditService {
    static Logger LOG = Logger.getLogger(TMACreditService.class);

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    TransactionRepository transRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    Sender sender;

    @Autowired
    MessageTransferModel messageTransferModel;

    final ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Value("${user.queueRequest}")
    private String queueRequest;

    public void depositSalary() {
        LOG.info("TMA payment salary for employee - In 5/*/*");
        List<UserInfo> userInfoList = userInfoRepository.findAll();
//        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (UserInfo userInfo : userInfoList) {
            executorService.execute(new SalaryWorker(userInfo, mongoTemplate));
        }
//        executorService.shutdown();
    }

    public void loanPaymentMonthly() {
        LOG.info("TMA system check loan payment monthly - In 1/*/*");
        List<UserInfo> userInfoList = userInfoRepository.findAll();

        for (UserInfo userInfo : userInfoList) {
            if (userInfo.getEmail() == null || userInfo.getTmaCredit().getBalance() >= userInfo.getTmaCredit().getLoanPayment()) {
                continue;
            }
            executorService.execute(new LoanWorker(userInfo, javaMailSender));
        }
//        executorService.shutdown();
    }

    public void withdraw(TransferOrder transaction, String correlationId, Destination destination) {
        executorService.execute(new WithdrawWorker.WithdrawWorkerBuilder(transaction)
                .setTransactionRepository(transRepository)
                .setMongoTemplate(mongoTemplate)
                .setSender(sender)
                .setMessageTransferObject(messageTransferModel)
                .setDestination(destination)
                .setCorrelationId(correlationId)
                .build());
    }

    public void deposit(TransferOrder transaction, String correlationId) {
        executorService.execute(new DepositWorker.DepositWorkerBuilder(transaction)
                .setMongoTemplate(mongoTemplate)
                .build());
    }


    //    public void reDeposit(Transaction transaction, String correlationId) {
//        Information info = infoRepository.findOne(transaction.getReceiverID());
//        boolean isSuccess = info.deposit(transaction.getValue());
//        if (isSuccess) {
//            try {
//                infoRepository.save(info);
//            } catch (OptimisticLockingFailureException e) {
//                LOG.info("RE-DEPOSIT-SALARY");
//                messageTransferModel.setValue(Header.RE_DEPOSIT, transaction);
//                sender.sendToQueue(queueRequest, messageTransferModel, correlationId);
//            }
//        }
//        else {
//            LOG.info("Deposit fail");
//        }
//    }
}
