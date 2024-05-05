package com.example.CS550;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class Cs550Application {

	public static void main(String[] args) {
		SpringApplication.run(Cs550Application.class, args);
	}

}
