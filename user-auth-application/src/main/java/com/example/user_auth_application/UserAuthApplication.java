package com.example.user_auth_application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
		"com.example.user",
		"com.example.auth"
})
@EntityScan(basePackages = {
		"com.example.user.entity",
		"com.example.auth.entity"
})
@EnableJpaRepositories(basePackages = {
		"com.example.user.repository",
		"com.example.auth.repository"
})
public class UserAuthApplication {
	public static void main(String[] args) {
		SpringApplication.run(UserAuthApplication.class, args);
	}
}
