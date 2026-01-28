package com.rihan.linkshortenerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan; // ✅ (Boot 4)
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.rihan.linkshortenerservice.url")
@EnableJpaRepositories(basePackages = "com.rihan.linkshortenerservice.url")
public class LinkShortenerServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(LinkShortenerServiceApplication.class, args);
	}
}
