package com.isfive.usearth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class UsEarthApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsEarthApplication.class, args);
	}

}
