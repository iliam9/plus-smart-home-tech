package ru.yandex.practicum.handler;

import com.google.protobuf.Timestamp;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimestampMapper {
    public static Instant mapToInstant(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }

}