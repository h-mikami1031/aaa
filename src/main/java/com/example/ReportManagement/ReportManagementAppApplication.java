package com.example.ReportManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.ReportManagement")
@EnableJpaRepositories(basePackages = "com.example.ReportManagement.repository")
@EntityScan(basePackages = "com.example.ReportManagement.entity")
public class ReportManagementAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportManagementAppApplication.class, args);
	}

}
