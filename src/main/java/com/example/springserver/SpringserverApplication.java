package com.example.springserver;

import com.example.springserver.dao.AssetDao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringserverApplication {

	@Bean
	public AssetDao assetDao() {
		return new AssetDao();
	}
	public static void main(String[] args) {
		SpringApplication.run(SpringserverApplication.class, args);
	}

}
