package com.duytran.repositories;

import com.duytran.entities.UserInfo;
import org.springframework.data.mongodb.repository.ExistsQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserInfoRepository extends MongoRepository<UserInfo, String> {
    @ExistsQuery(value = "{'id': ?0}")
    boolean checkExistById(String id);
}
