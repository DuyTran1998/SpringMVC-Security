package com.duytran.repositories;

import com.duytran.entities.Device;
import org.springframework.data.mongodb.repository.ExistsQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface DeviceRepository extends MongoRepository<Device, String> {

    @Query(value="{'id' : ?0}", delete = true)
    void deleteById (String id);

    @Query(value="{'id' : ?0}")
    Optional<Device> findDeviceById(String id);

    @ExistsQuery(value = "{'macAddress': ?0}")
    boolean existDeviceByMacAddress(String macAddress);

    @ExistsQuery(value = "{'ipAddress': ?0}")
    boolean existDeviceByIpAddress(String ipAddress);
}
