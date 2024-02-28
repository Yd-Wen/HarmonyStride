package com.srdp.harmonystride.util;

import android.os.Build;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    // 将时间戳转换为LocalDateTime
    public static LocalDateTime timestampToLocalDateTime(long timestamp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);
        }
        return null;
    }

    // 将LocalDateTime转换为指定格式的字符串
    public static String formatLocalDateTime(LocalDateTime dateTime, String format) {
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern(format);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return dateTime.format(formatter);
        }
        return null;
    }
}

