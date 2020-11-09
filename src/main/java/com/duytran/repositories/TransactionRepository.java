package com.duytran.repositories;

import com.duytran.entities.TransferOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<TransferOrder, String> {
    
}
