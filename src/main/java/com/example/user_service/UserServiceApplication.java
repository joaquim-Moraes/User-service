package com.example.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();

    // PUXAR CADA VARIÁVEL DO .env E "ENTREGAR" PARA O SPRING
    //    Isso resolve o "PlaceholderResolutionException"
		System.setProperty("SENDGRID_API_KEY", dotenv.get("SENDGRID_API_KEY"));
		System.setProperty("SENDGRID_FROM_EMAIL", dotenv.get("SENDGRID_FROM_EMAIL"));
		System.setProperty("SENDGRID_TEMPLATE_ID", dotenv.get("SENDGRID_TEMPLATE_ID"));
		// Puxando as variaveis do env
		System.setProperty("DB_URL", dotenv.get("DB_URL"));
		System.setProperty("DB_USER", dotenv.get("DB_USER"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		// Puxando a chave secret
		System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));

		
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
