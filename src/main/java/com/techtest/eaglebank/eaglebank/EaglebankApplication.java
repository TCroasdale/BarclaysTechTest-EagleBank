package com.techtest.eaglebank.eaglebank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.baeldung.openapi.api.V1ApiController;

@SpringBootApplication
@Import({ V1ApiController.class })
public class EaglebankApplication {

	public static void main(String[] args) {
		SpringApplication.run(EaglebankApplication.class, args);
	}

}
