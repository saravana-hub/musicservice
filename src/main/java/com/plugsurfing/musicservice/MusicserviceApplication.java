package com.plugsurfing.musicservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MusicserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MusicserviceApplication.class, args);
	}
}
