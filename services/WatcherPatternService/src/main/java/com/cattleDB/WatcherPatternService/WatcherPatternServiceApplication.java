package com.cattleDB.WatcherPatternService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WatcherPatternServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WatcherPatternServiceApplication.class, args);
	}

}
