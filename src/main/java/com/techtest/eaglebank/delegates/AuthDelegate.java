package com.techtest.eaglebank.delegates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.baeldung.openapi.api.ApiUtil;
import com.baeldung.openapi.api.V1Api;
import com.baeldung.openapi.api.V1ApiDelegate;
import com.baeldung.openapi.model.AuthorizationRequest;
import com.baeldung.openapi.model.AuthorizationResponse;
import com.baeldung.openapi.model.BankAccountResponse;
import com.baeldung.openapi.model.CreateBankAccountRequest;
import com.baeldung.openapi.model.CreateUserRequest;
import com.baeldung.openapi.model.UserResponse;
import com.techtest.eaglebank.DatabaseService;
import com.techtest.eaglebank.JwtService;
import com.techtest.eaglebank.entities.User;
import com.techtest.eaglebank.repositories.UserRepository;

public class AuthDelegate implements V1ApiDelegate {
    
    @Autowired
    DatabaseService databaseService;

    @Autowired
    JwtService jwtService;
    
    /**
     * POST /v1/auth
     * Issue a new JWT
     *
     * @param authorizationRequest Authorize a user an issue a new JWT (required)
     * @return Bank Account has been created successfully (status code 200)
     *         or Invalid details supplied (status code 400)
     *         or An unexpected error occurred (status code 500)
     * @see V1Api#authorize
     */
    @Override
    public ResponseEntity<AuthorizationResponse> authorize(AuthorizationRequest authorizationRequest) {
        User u = databaseService.getUserFromUserID(authorizationRequest.getUserid());
        if (u == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            String token = jwtService.IssueToken(u);
            AuthorizationResponse resp = new AuthorizationResponse(token);
            return ResponseEntity.ok(resp);
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
