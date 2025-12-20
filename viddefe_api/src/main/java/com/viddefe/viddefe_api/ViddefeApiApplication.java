package com.viddefe.viddefe_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableAsync
public class ViddefeApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ViddefeApiApplication.class, args);
	}
	@GetMapping
	public String index() {
		return "Hello World";
	}
}
