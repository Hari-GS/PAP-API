package com.example.PAP_API;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PapApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PapApiApplication.class, args);
	}
}
