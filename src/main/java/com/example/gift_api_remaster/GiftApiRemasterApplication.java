package com.example.gift_api_remaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GiftApiRemasterApplication {

	public static void main(String[] args) {
		SpringApplication.run(GiftApiRemasterApplication.class, args);
	}

}
