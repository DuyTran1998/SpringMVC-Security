package com.duytran.repositories;

import com.duytran.entities.User;
import org.springframework.data.mongodb.repository.ExistsQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    @Query("{'username' : ?0}")
    User findUserByUserName(String username);

    @Query("{'id' : ?0}")
    Optional<User> findUserById(String id);

    @Query("{'username' : ?0}")
    List<User> findUserByName(String username);

    @Query("{'username' : ?0, 'password' : ?1} ")
    User findUserByUserNameAndPassWord(String username, String password);

    @ExistsQuery(value = "{'username': ?0}")
    boolean checkExistUserName(String username);

    List<User> findAll();
}
