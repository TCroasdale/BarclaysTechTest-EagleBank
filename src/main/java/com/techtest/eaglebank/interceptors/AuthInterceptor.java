package com.techtest.eaglebank.interceptors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.techtest.eaglebank.DatabaseService;
import com.techtest.eaglebank.JwtService;
import com.techtest.eaglebank.entities.User;
import com.techtest.eaglebank.entities.Session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configurable
public class AuthInterceptor implements HandlerInterceptor {
    @Autowired JwtService jwtService;
    @Autowired DatabaseService db;

    final List<String> whitelist = Arrays.asList("POST;/v1/users", "POST;/v1/auth");
    
    private boolean checkAuthWhiteList(HttpServletRequest request) {
        return whitelist.contains(request.getMethod() + ";" + request.getRequestURI());
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse  response, Object handlerObject) {
        if (checkAuthWhiteList(request)) return true;

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            return false;
        }

        final String jwt = authHeader.substring(7);
        Session userSession;
        boolean result;
        try {
            userSession = jwtService.DecodeToken(jwt);
            result = jwtService.CheckToken(userSession);

            User u = db.getUserFromUserID(userSession.userid);
            request.setAttribute("user", u);
        }
        catch (Exception e) {
            result = false;
        }

        if (!result) {
            response.setStatus(401);
        } 

        return result;
    }
} 