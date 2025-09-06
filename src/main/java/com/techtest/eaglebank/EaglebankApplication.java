package com.techtest.eaglebank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.baeldung.openapi.api.V1ApiController;
import com.techtest.eaglebank.delegates.ApiDelegate;
import com.techtest.eaglebank.interceptors.AuthInterceptor;

@SpringBootApplication
public class EaglebankApplication {

	@Autowired APIController apiController;
	@Autowired ApiDelegate apiDelegate;

	public static void main(String[] args) {
		SpringApplication.run(EaglebankApplication.class, args);
	}

}
