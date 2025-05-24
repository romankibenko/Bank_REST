package com.example.bankcards.util;

import com.example.bankcards.service.DataMigrationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataMigrationRunner implements CommandLineRunner {

    private final DataMigrationService dataMigrationService;

    public DataMigrationRunner(DataMigrationService dataMigrationService) {
        this.dataMigrationService = dataMigrationService;
    }

    @Override
    public void run(String... args) {
        dataMigrationService.encryptExistingCardNumbers();
    }
}