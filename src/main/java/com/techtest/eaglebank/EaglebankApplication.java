package com.techtest.eaglebank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.baeldung.openapi.api.V1ApiController;
import com.techtest.eaglebank.delegates.UserDelegate;

@SpringBootApplication
@Import({ V1ApiController.class, UserDelegate.class })
public class EaglebankApplication {

	public static void main(String[] args) {
		SpringApplication.run(EaglebankApplication.class, args);
	}

}
