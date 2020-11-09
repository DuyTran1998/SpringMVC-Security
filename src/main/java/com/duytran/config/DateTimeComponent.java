package com.duytran.config;

import com.duytran.converter.ZonedDateTimeWriteConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class DateTimeComponent {
    @Autowired
    ZonedDateTimeWriteConverter zonedDateTimeWriteConverter;

    public final LocalDateTime getLocalDateTimeNow() {
        String ZONE_ID = "Asia/Ho_Chi_Minh";
        return LocalDateTime.now(ZoneId.of(ZONE_ID));
    }

    public final Date getDateTimeNow() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.of("+07:00"));
        return zonedDateTimeWriteConverter.convert(now);
    }

}
