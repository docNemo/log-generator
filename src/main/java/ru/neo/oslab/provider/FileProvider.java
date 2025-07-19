package ru.neo.oslab.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class FileProvider {
    private static final boolean APPEND_MODE = true;
    private final String fileName;

    public void startLogFiles() {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(fileName))) {
            writer.println("[");

            log.info("Save start file logs to {}", fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearFile() {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(fileName))) {
            writer.print("");

            log.info("Save start file logs to {}", fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void finishLogFiles() {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(fileName, APPEND_MODE))) {
            writer.println("]");

            log.info("Save finish file logs to {}", fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveLogsWithComma(List<String> logs) {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(fileName, APPEND_MODE))) {
            for (String s : logs) {
                writer.println(s + ",");
            }

            log.info("Save {} logs to {}", logs.size(), fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveLogs(List<String> logs) {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(fileName, APPEND_MODE))) {
            for (String s : logs) {
                writer.println(s);
            }

            log.info("Save {} logs to {}", logs.size(), fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveReport(Map<String, Map<String, Integer>> frequency) {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream("report-" + fileName))) {

            frequency.forEach(
                    (app, messages) -> {
                        writer.println(app);
                        messages.forEach(
                                (message, number) -> writer.printf("%s: %d\n", message, number)
                        );
                        writer.println();
                    }
            );

            log.info("Save report to {}", fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
