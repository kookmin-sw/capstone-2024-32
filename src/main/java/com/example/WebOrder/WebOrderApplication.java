package com.example.WebOrder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebOrderApplication.class, args);
	}

}
