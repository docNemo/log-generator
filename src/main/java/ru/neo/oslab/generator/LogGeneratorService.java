package ru.neo.oslab.generator;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.neo.oslab.dto.Log;
import ru.neo.oslab.dto.Pair;
import ru.neo.oslab.provider.FileProvider;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
public class LogGeneratorService {
    private static final List<String> APP_NAMES = List.of(
            "arachni-articles",
            "virage-tickets",
            "security-management",
            "task-scheduler",
            "BigBongApp",
            "Billi_Bobba",
            "Boba Fetta",
            "pizza-pesto-order-tracker"
    );

    private static final List<Pair<String, Boolean>> LOG_MESSAGE = List.of(
            new Pair<>("Failed to connect to database", false),
            new Pair<>("NullPointerException at UserService.java:45", false),
            new Pair<>("Error while processing payment for order %s", true),
            new Pair<>("Unable to send email to user %s", true),
            new Pair<>("File %s not found", true),
            new Pair<>("Timeout while calling external API", false),
            new Pair<>("Authentication request %s failed for user: admin", true),
            new Pair<>("Insufficient %s permissions to access resource", true),
            new Pair<>("Unexpected exception in scheduled task %s", true),
            new Pair<>("Failed to load configuration %s file", true),
            new Pair<>("Error parsing JSON response", false),
            new Pair<>("Service unavailable, retry later", false),
            new Pair<>("OutOfMemoryError encountered", false),
            new Pair<>("Failed to initialize application context", false),
            new Pair<>("Data integrity %s violation detected", true),
            new Pair<>("User session %s expired unexpectedly", true),
            new Pair<>("Error updating user profile", false),
            new Pair<>("Failed to delete temporary files", false),
            new Pair<>("Connection %s reset by peer", true),
            new Pair<>("Error during data migration %s", true),
            new Pair<>("Unable to parse request parameters", false),
            new Pair<>("Database transaction rolled back", false),
            new Pair<>("Critical system error: shutting down", false),
            new Pair<>("%s: Failed to authenticate user", true),
            new Pair<>("Error writing to log file", false),
            new Pair<>("Payment gateway error: invalid response", false),
            new Pair<>("Service crashed due to unhandled exception", false),
            new Pair<>("Failed to load user preferences", false),
            new Pair<>("Error during file upload", false),
            new Pair<>("Unexpected server error occurred: %s", true),
            new Pair<>("Unable to resolve hostname", false),
            new Pair<>("Error while processing request", false),
            new Pair<>("Failed to start background job", false),
            new Pair<>("Security exception: unauthorized access", false),
            new Pair<>("Error during cache eviction", false),
            new Pair<>("Failed to generate report", false),
            new Pair<>("Critical failure in payment processing", false),
            new Pair<>("Failed: Connection failed: Unable to connect to external service", false),
            new Pair<>("Error in data serialization", false),
            new Pair<>("Application crashed unexpectedly", false),
            new Pair<>("Failed to parse configuration", false),
            new Pair<>("Error during user %s registration", true),
            new Pair<>("Unable to retrieve data from cache", false),
            new Pair<>("Service %s response invalid", true),
            new Pair<>("Error during session %s cleanup", true),
            new Pair<>("Critical bug detected in module", false),
            new Pair<>("System shutdown due to critical error", false)
    );

    private static final List<String> LOGGERS = List.of(
            "org.apache.AppLogger",
            "ru.point.saveCustomLogger",
            "com.file.owl,LogManager",
            "cz.hip.ServiceLogger",
            "com.minor.AuditLogger",
            "ru.edf.pdf.ErrorLogger",
            "org.queue.DebugLogger",
            "org.gog.FileLogger",
            "ru.hub.trio.NotificationLogger",
            "com.joker.enterprise.EventLogger"
    );

    private final Gson gson;
    private final FileProvider fileProvider;
    private final Random random;
    private final int logsNumber;

    public void generateLogs() {
        LocalDateTime start = LocalDateTime.now();
        Map<String, Map<String, Integer>> frequency = new ConcurrentHashMap<>();

        log.info("Start generation");
        List<String> logs = IntStream
                .range(0, logsNumber)
                .parallel()
                .mapToObj(_ -> new Log())
                .map(this::fillApp)
                .map(this::fillMessage)
                .map(this::fillLogger)
                .map(this::fillDate)
                .map(log -> countFrequency(log, frequency))
                .map(gson::toJson)
                .toList();

        LocalDateTime finishedGeneration = LocalDateTime.now();
        log.info("Finished generation for {}", ChronoUnit.MICROS.between(start, finishedGeneration));

        log.info("В файле сумма сообщений {} при запрошенных {}", checkFrequency(frequency), logsNumber);

        fileProvider.saveLogs(logs);
        fileProvider.saveReport(frequency);

        LocalDateTime finishedSaving = LocalDateTime.now();
        log.info("Finished saving for {}", ChronoUnit.MICROS.between(finishedGeneration, finishedSaving));
        log.info("Finished for {}", ChronoUnit.MICROS.between(start, finishedSaving));
    }

    Log fillApp(Log log) {
        String app = APP_NAMES.get(random.nextInt(APP_NAMES.size()));
        log.setApp(app);
        return log;
    }

    Log fillMessage(Log log) {
        Pair<String, Boolean> messagePair = LOG_MESSAGE.get(random.nextInt(APP_NAMES.size()));
        String message = messagePair.getRight()
                ? messagePair.getLeft().formatted(UUID.randomUUID())
                : messagePair.getLeft();
        log.setMessage(message);
        return log;
    }

    Log fillLogger(Log log) {
        String logger = LOGGERS.get(random.nextInt(APP_NAMES.size()));
        log.setLogger(logger);
        return log;
    }

    Log fillDate(Log log) {
        log.setDate(
                LocalDateTime
                        .now()
                        .minusDays(random.nextInt(100))
                        .minusHours(random.nextInt(12))
                        .minusMinutes(random.nextInt(59))
                        .minusSeconds(random.nextInt(59))
                        .minusNanos(random.nextInt(999999))

        );
        return log;
    }

    Log countFrequency(Log log, Map<String, Map<String, Integer>> frequency) {
        Map<String, Integer> newMessage = new HashMap<>();
        newMessage.put(log.getClearMessage(), 1);

        frequency.merge(
                log.getApp(),
                newMessage,
                (a, b) -> {
                    b.keySet().forEach(key -> a.merge(key, b.get(key), Integer::sum));
                    return a;
                }
        );

        return log;
    }

    int checkFrequency(Map<String, Map<String, Integer>> frequency) {
        return frequency
                .values()
                .stream()
                .flatMap(messages -> messages.values().stream())
                .reduce(Integer::sum)
                .orElse(-1);
    }
}
