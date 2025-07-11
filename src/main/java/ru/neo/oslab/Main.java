package ru.neo.oslab;

import ru.neo.oslab.factory.LogGeneratorServiceFactory;

public class Main {
    public static void main(String[] args) {
        new LogGeneratorServiceFactory()
                .logGeneratorService()
                .generateLogs(5000000);
    }
}