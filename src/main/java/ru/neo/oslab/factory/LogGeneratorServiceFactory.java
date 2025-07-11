package ru.neo.oslab.factory;

import com.google.gson.Gson;
import ru.neo.oslab.generator.LogGeneratorService;
import ru.neo.oslab.provider.FileProvider;

import java.time.LocalDate;
import java.util.Random;

public class LogGeneratorServiceFactory {
    public LogGeneratorService logGeneratorService(int logsNumber) {

        return new LogGeneratorService(
                new Gson(),
                new FileProvider(LocalDate.now() + ".log"),
                new Random(),
                logsNumber
        );
    }
}
