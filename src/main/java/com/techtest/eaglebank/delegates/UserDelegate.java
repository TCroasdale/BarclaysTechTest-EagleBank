package com.techtest.eaglebank.delegates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.baeldung.openapi.api.ApiUtil;
import com.baeldung.openapi.api.V1Api;
import com.baeldung.openapi.api.V1ApiDelegate;
import com.baeldung.openapi.model.BankAccountResponse;
import com.baeldung.openapi.model.CreateBankAccountRequest;
import com.baeldung.openapi.model.CreateUserRequest;
import com.baeldung.openapi.model.UserResponse;
import com.techtest.eaglebank.DatabaseService;
import com.techtest.eaglebank.entities.User;
import com.techtest.eaglebank.repositories.UserRepository;

public class UserDelegate implements V1ApiDelegate {
    
    @Autowired
    DatabaseService databaseService;
    
    /**
     * POST /v1/users
     * Create a new user
     *
     * @param createUserRequest Create a new user (required)
     * @return User has been created successfully (status code 201)
     *         or Invalid details supplied (status code 400)
     *         or An unexpected error occurred (status code 500)
     * @see V1Api#createUser
     */
    @Override
    public ResponseEntity<UserResponse> createUser(CreateUserRequest createUserRequest) {
        
        if (createUserRequest.getName().isBlank() ||
            createUserRequest.getEmail().isBlank() ||
            createUserRequest.getPhoneNumber().isBlank() ||
            createUserRequest.getAddress().getLine1().isBlank() ||
            createUserRequest.getAddress().getTown().isBlank() ||
            createUserRequest.getAddress().getCounty().isBlank() ||
            createUserRequest.getAddress().getPostcode().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        String address = createUserRequest.getAddress().getLine1() + ";" + 
                        createUserRequest.getAddress().getLine2() + ";" +
                        createUserRequest.getAddress().getLine3() + ";" +
                        createUserRequest.getAddress().getTown() + ";" +
                        createUserRequest.getAddress().getCounty() + ";" +
                        createUserRequest.getAddress().getPostcode();

        User u = new User();
        u.name = createUserRequest.getName();
        u.email = createUserRequest.getEmail();
        u.phoneNumber = createUserRequest.getPhoneNumber();
        u.address = address;
        u.userid = "usr-" + databaseService.getUserCount();
        databaseService.saveUser(u);

        UserResponse resp = new UserResponse(u.userid, u.name, createUserRequest.getAddress(), u.phoneNumber, u.email, u.createdTimestamp, u.updatedTimestamp);
        return ResponseEntity.ok(resp);
    }
}
