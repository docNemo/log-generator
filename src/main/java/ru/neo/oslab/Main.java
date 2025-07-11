package ru.neo.oslab;

import ru.neo.oslab.factory.LogGeneratorServiceFactory;

public class Main {
    public static void main(String[] args) {
        new LogGeneratorServiceFactory()
                .logGeneratorService(5000000)
                .generateLogs();
    }
}