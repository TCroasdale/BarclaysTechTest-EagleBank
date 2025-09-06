package com.techtest.eaglebank.delegates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.baeldung.openapi.api.V1ApiDelegate;
import com.baeldung.openapi.model.AuthorizationRequest;
import com.baeldung.openapi.model.AuthorizationResponse;
import com.baeldung.openapi.model.CreateUserRequest;
import com.baeldung.openapi.model.UserResponse;

@Import({ AuthDelegate.class, UserDelegate.class })
@Component
public class ApiDelegate implements V1ApiDelegate {
    @Autowired AuthDelegate authDelegate;
    @Autowired UserDelegate userDelegate;

    public ResponseEntity<UserResponse> createUser(CreateUserRequest cur) {
        return userDelegate.createUser(cur);
    }

    public ResponseEntity<AuthorizationResponse> authorize(AuthorizationRequest ar) {
        return authDelegate.authorize(ar);
    }

    public ResponseEntity<UserResponse> fetchUserByID(String userId) {
        return userDelegate.fetchUserByID(userId);
    }
}
