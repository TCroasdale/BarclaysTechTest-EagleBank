package com.techtest.eaglebank.delegates;

import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import com.baeldung.openapi.api.ApiUtil;
import com.baeldung.openapi.api.V1Api;
import com.baeldung.openapi.api.V1ApiDelegate;
import com.baeldung.openapi.model.BankAccountResponse;
import com.baeldung.openapi.model.CreateBankAccountRequest;
import com.baeldung.openapi.model.CreateUserRequest;
import com.baeldung.openapi.model.CreateUserRequestAddress;
import com.baeldung.openapi.model.UserResponse;
import com.techtest.eaglebank.DatabaseService;
import com.techtest.eaglebank.entities.User;
import com.techtest.eaglebank.repositories.UserRepository;

public class UserDelegate implements V1ApiDelegate {
    
    @Autowired
    DatabaseService databaseService;


    @Autowired NativeWebRequest nativeWebRequest;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.of(nativeWebRequest);
    }
    
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

    /**
     * GET /v1/users/{userId}
     * Fetch user by ID.
     *
     * @param userId ID of the user (required)
     * @return The user details (status code 200)
     *         or The request didn&#39;t supply all the necessary data (status code 400)
     *         or Access token is missing or invalid (status code 401)
     *         or The user is not allowed to access the transaction (status code 403)
     *         or User was not found (status code 404)
     *         or An unexpected error occurred (status code 500)
     * @see V1Api#fetchUserByID
     */
    public ResponseEntity<UserResponse> fetchUserByID(String userId) {
        User sessionUser = null;
        if (getRequest().isPresent()) {
            sessionUser = (User)getRequest().get().getAttribute("user", 0);
        }
        if (sessionUser == null) {
            return ResponseEntity.status(401).build();
        }
        if (userId != sessionUser.userid) {
            return ResponseEntity.status(401).build();
        }
            
        User u = databaseService.getUserFromUserID(userId);
        if (u == null) {
            return ResponseEntity.notFound().build();
        }

        UserResponse resp = new UserResponse(u.userid, u.name, new CreateUserRequestAddress(), u.phoneNumber, u.email, u.createdTimestamp, u.updatedTimestamp);
        return ResponseEntity.ok(resp);
    }
}
