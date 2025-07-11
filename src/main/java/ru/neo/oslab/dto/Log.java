package ru.neo.oslab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Log {
    String app;
    String message;
    LocalDateTime date;
    String logger;

    public String getClearMessage() {
        return this.message
                .replaceAll("[a-zA-Z0-9_-]{30,}", "");
    }
}
