package com.hatake.cattleDB;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
//@EnableScheduling // Enable Spring's scheduled task execution
public class CattleDbApplication {

	public static void main(String[] args) {
		SpringApplication.run(CattleDbApplication.class, args);
	}

}
