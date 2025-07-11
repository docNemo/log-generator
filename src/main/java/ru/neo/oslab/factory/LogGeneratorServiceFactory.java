package ru.neo.oslab.factory;

import com.google.gson.GsonBuilder;
import ru.neo.oslab.adapter.LocalDateTimeAdapter;
import ru.neo.oslab.generator.LogGeneratorService;
import ru.neo.oslab.provider.FileProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

public class LogGeneratorServiceFactory {
    public LogGeneratorService logGeneratorService(int logsNumber) {

        return new LogGeneratorService(
                new GsonBuilder()
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                        .create(),
                new FileProvider(LocalDate.now() + ".log"),
                new Random(),
                logsNumber
        );
    }
}
