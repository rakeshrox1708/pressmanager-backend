package com.newspaper.System;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class NewspaperSystemApplication {


	public static void main(String[] args) {

		System.out.println("PassWord of admin id : = " + new BCryptPasswordEncoder().encode("Admin@123"));
		SpringApplication.run(NewspaperSystemApplication.class, args);
	}

}
