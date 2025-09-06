package com.techtest.eaglebank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.techtest.eaglebank.delegates.ApiDelegate;

@SpringBootApplication
public class EaglebankApplication {

	@Autowired APIController apiController;
	@Autowired ApiDelegate apiDelegate;

	public static void main(String[] args) {
		SpringApplication.run(EaglebankApplication.class, args);
	}

}
