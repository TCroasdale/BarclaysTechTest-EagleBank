package com.techtest.eaglebank;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.techtest.eaglebank.interceptors.AuthInterceptor;

@Configuration
public class AppConfig extends WebMvcConfigurationSupport {

    @Bean
    public AuthInterceptor authinterceptor() {
        return new AuthInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authinterceptor()).addPathPatterns("/**");
    }
}