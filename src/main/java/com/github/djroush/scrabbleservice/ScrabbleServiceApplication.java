package com.github.djroush.scrabbleservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ScrabbleServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ScrabbleServiceApplication.class, args);
	}
}
