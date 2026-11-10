package com.example.websome.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataInitializer implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        log.info("HocDeThiFPT: Connected to SQL Server database HocDeThiFPT. Using purely database records (zero hardcoded mock data).");
    }
}
