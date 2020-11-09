package com.duytran.services.device;

import com.duytran.activemq.Sender;
import com.duytran.constant.Message;
import com.duytran.entities.Device;
import com.duytran.models.ResponseModel;
import com.duytran.repositories.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.jms.Destination;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DeviceService {
    private final String IPV4_REGEX = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    private final String MAC_REGEX = "^([0-9A-Fa-f]{2}[:-])"
            + "{5}([0-9A-Fa-f]{2})|"
            + "([0-9a-fA-F]{4}\\."
            + "[0-9a-fA-F]{4}\\."
            + "[0-9a-fA-F]{4})$";

    private final String TIME_REGEX = "\\d{4}-\\d{2}-\\d{2}T\\d{1,2}:\\d{2}:\\d{2}.\\d{3}Z";

    @Autowired
    Sender sender;

    @Autowired
    DeviceRepository deviceRepository;


    public void createDevice(Device device, String correlationId, Destination destination) {
        if (validateInformationDevice(device.getIpAddress(), IPV4_REGEX)) {
            sender.replyMessage(destination, new ResponseModel(HttpStatus.BAD_REQUEST.value(),
                    Message.IPADDRESS_INVALID, device), correlationId);
            return;
        }

        if (validateInformationDevice(device.getMacAddress(), MAC_REGEX)) {
            sender.replyMessage(destination, new ResponseModel(HttpStatus.BAD_REQUEST.value(),
                    Message.MAC_ADDRESS_INVALID, device), correlationId);
            return;
        }

        if (validateInformationDevice(device.getManagedIp(), IPV4_REGEX)) {
            sender.replyMessage(destination, new ResponseModel(HttpStatus.BAD_REQUEST.value(),
                    Message.MANAGED_IP_INVALID, device), correlationId);
            return;
        }

//        if (validateInformationDevice(device.getDiscoveredDateTime().toString(), TIME_REGEX)){
//            sender.replyMessage(destination, new ResponseModel(HttpStatus.BAD_REQUEST.value(),
//                    Message.DISCOVERED_DATETIME_INVALID, device), correlationId);
//            return;
//        }
//
//        if (validateInformationDevice(device.getLastKnownUpAt().toString(), TIME_REGEX)){
//            sender.replyMessage(destination, new ResponseModel(HttpStatus.BAD_REQUEST.value(),
//                    Message.LAST_KNOWN_UP_AT_INVALID, device), correlationId);
//            return;
//        }
//
//        if (validateInformationDevice(device.getModifiedDate().toString(), TIME_REGEX)){
//            sender.replyMessage(destination, new ResponseModel(HttpStatus.BAD_REQUEST.value(),
//                    Message.MODIFIED_DATE_INVALID, device), correlationId);
//            return;
//        }
        if (device.getModifiedDate() == null || device.getLastKnownUpAt() == null ||
                device.getDiscoveredDateTime() == null) {
            sender.replyMessage(destination, new ResponseModel(HttpStatus.BAD_REQUEST.value(),
                    Message.DATETIME_INVALID, device), correlationId);
            return;
        }



        if (deviceRepository.existDeviceByIpAddress(device.getIpAddress())) {
            sender.replyMessage(destination, new ResponseModel(HttpStatus.BAD_REQUEST.value(),
                    Message.IPADDRESS_EXISTS, device), correlationId);
            return;
        }

        if (deviceRepository.existDeviceByMacAddress(device.getMacAddress())) {
            sender.replyMessage(destination, new ResponseModel(HttpStatus.BAD_REQUEST.value(),
                    Message.MAC_ADDRESS_EXISTS, device), correlationId);
            return;
        }

        try {
            deviceRepository.insert(device);
            sender.replyMessage(destination, new ResponseModel(HttpStatus.OK.value(),
                    Message.SUCCESS, device), correlationId);
        } catch (Exception e) {
            sender.replyMessage(destination,new ResponseModel(HttpStatus.BAD_REQUEST.value(),
                    Message.FAIL, device), correlationId);
        }
    }

    public void updateDevice(Device device, String correlationId, Destination destination) {
        if (validateInformationDevice(device.getIpAddress(), IPV4_REGEX)) {
            sender.replyMessage(destination,new ResponseModel(HttpStatus.BAD_REQUEST.value(),
                    Message.IPADDRESS_INVALID, device), correlationId);
            return;
        }

        if (validateInformationDevice(device.getMacAddress(), MAC_REGEX)) {
            sender.replyMessage(destination,new ResponseModel(HttpStatus.BAD_REQUEST.value(),
                    Message.MAC_ADDRESS_INVALID, device), correlationId);
            return;
        }

        if (validateInformationDevice(device.getManagedIp(), IPV4_REGEX)) {
            sender.replyMessage(destination, new ResponseModel(HttpStatus.BAD_REQUEST.value(),
                    Message.MANAGED_IP_INVALID, device), correlationId);
            return;
        }

        if (device.getModifiedDate() == null || device.getLastKnownUpAt() == null ||
                device.getDiscoveredDateTime() == null) {
            sender.replyMessage(destination, new ResponseModel(HttpStatus.BAD_REQUEST.value(),
                    Message.DATETIME_INVALID, device), correlationId);
            return;
        }

        Optional<Device> optionalDevice = deviceRepository.findDeviceById(device.getId());
        if (optionalDevice.isPresent()) {
            Device updateDevice = optionalDevice.get();
            if (updateDevice.getIpAddress().equals(device.getIpAddress())
                    && updateDevice.getMacAddress().equals(device.getMacAddress())) {
                deviceRepository.save(device);
                sender.replyMessage(destination, new ResponseModel(HttpStatus.OK.value(),
                        Message.SUCCESS, device), correlationId);
                return;
            }
            if (!updateDevice.getIpAddress().equals(device.getIpAddress())) {
                if (deviceRepository.existDeviceByIpAddress(device.getIpAddress())) {
                    sender.replyMessage(destination, new ResponseModel(HttpStatus.BAD_REQUEST.value(),
                            Message.IPADDRESS_EXISTS, device), correlationId);
                    return;
                }
            }
            if (!updateDevice.getMacAddress().equals(device.getMacAddress())) {
                if (deviceRepository.existDeviceByMacAddress(device.getMacAddress())) {
                    sender.replyMessage(destination, new ResponseModel(HttpStatus.BAD_REQUEST.value(),
                            Message.MAC_ADDRESS_EXISTS, device), correlationId);
                    return;
                }
            }

            try {
                deviceRepository.save(device);
                sender.replyMessage(destination, new ResponseModel(HttpStatus.OK.value(),
                        Message.SUCCESS, device), correlationId);
            } catch (Exception e) {
                sender.replyMessage(destination, new ResponseModel(HttpStatus.BAD_REQUEST.value(),
                        Message.FAIL, device), correlationId);
            }
        } else {
            sender.replyMessage(destination,new ResponseModel(HttpStatus.NOT_FOUND.value(),
                    Message.NOT_FOUND, device), correlationId);
        }
    }

    public void deleteDevice(String id, String correlationId, Destination destination) {
        if (deviceRepository.exists(id)) {
            try {
                deviceRepository.deleteById(id);
                sender.replyMessage(destination, new ResponseModel(HttpStatus.OK.value(),
                        Message.SUCCESS, id), correlationId);
            } catch (Exception e) {
                sender.replyMessage(destination, new ResponseModel(HttpStatus.BAD_REQUEST.value(),
                        Message.FAIL, id), correlationId);
            }
        } else {
            sender.replyMessage(destination, new ResponseModel(HttpStatus.NOT_FOUND.value(),
                    Message.NOT_FOUND, id), correlationId);
        }
    }

    public void getDevice(String id, String correlationId, Destination destination) {
        Optional<Device> device = deviceRepository.findDeviceById(id);
        if (device.isPresent()) {
            try {
                sender.replyMessage(destination, new ResponseModel(HttpStatus.OK.value(),
                        Message.SUCCESS, device.get()), correlationId);
            } catch (Exception e) {
                sender.replyMessage(destination, new ResponseModel(HttpStatus.BAD_REQUEST.value(),
                        Message.FAIL, id), correlationId);
            }
        } else {
            sender.replyMessage(destination, new ResponseModel(HttpStatus.NOT_FOUND.value(),
                    Message.NOT_FOUND, id), correlationId);
        }
    }

    public boolean validateInformationDevice(String info, String REGEX) {
        Pattern pattern = Pattern.compile(REGEX);
        if (info == null) {
            return true;
        }
        Matcher matcher = pattern.matcher(info);
        return !matcher.matches();
    }
}