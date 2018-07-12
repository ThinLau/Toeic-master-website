package com.toeicmaster.springmvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = { "com.toeicmaster.springmvc" })
@EnableConfigurationProperties(com.toeicmaster.springmvc.service.StorageProperties.class)
public class ToeicMasterWebsiteApplication extends SpringBootServletInitializer {
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ToeicMasterWebsiteApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(ToeicMasterWebsiteApplication.class, args);
	}
}
