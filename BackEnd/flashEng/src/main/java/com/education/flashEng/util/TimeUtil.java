package com.education.flashEng.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TimeUtil {
    public LocalDateTime addFractionOfDay(LocalDateTime time, double day) {
        long wholeDays = (long) day;
        double fractionalDays = day - wholeDays;
        long fractionalMinutes = (long) (fractionalDays * 24 * 60);
        return time.plusDays(wholeDays).plusMinutes(fractionalMinutes);
    }
}
