package com.sischi.selvebridge;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableConfigurationProperties
@SpringBootApplication
public class SelveBridgeApplication implements CommandLineRunner {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SelveBridgeApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		
	}
	
	

}
