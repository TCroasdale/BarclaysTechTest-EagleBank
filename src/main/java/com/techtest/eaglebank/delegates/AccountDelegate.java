package com.techtest.eaglebank.delegates;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import com.baeldung.openapi.api.V1Api;
import com.baeldung.openapi.api.V1ApiDelegate;
import com.baeldung.openapi.model.BankAccountResponse;
import com.baeldung.openapi.model.CreateBankAccountRequest;
import com.techtest.eaglebank.DatabaseService;
import com.techtest.eaglebank.entities.Account;
import com.techtest.eaglebank.entities.User;

public class AccountDelegate implements V1ApiDelegate {
    
    @Autowired
    DatabaseService databaseService;


    @Autowired NativeWebRequest nativeWebRequest;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.of(nativeWebRequest);
    }
    
    /**
     * POST /v1/accounts
     * Create a new bank account
     *
     * @param createBankAccountRequest Create a new bank account for the user (required)
     * @return Bank Account has been created successfully (status code 201)
     *         or Invalid details supplied (status code 400)
     *         or Access token is missing or invalid (status code 401)
     *         or The user is not allowed to access the transaction (status code 403)
     *         or An unexpected error occurred (status code 500)
     * @see V1Api#createAccount
     */
    @Override
    public ResponseEntity<BankAccountResponse> createAccount(CreateBankAccountRequest createBankAccountRequest) {
        User sessionUser = null;
        if (getRequest().isPresent()) {
            sessionUser = (User)getRequest().get().getAttribute("user", 0);
        }
        if (sessionUser == null) {
            return ResponseEntity.status(401).build();
        }

        if (createBankAccountRequest.getName().isBlank()) {
            return ResponseEntity.status(400).build();

        }

        Account a = new Account();
        a.accountName = createBankAccountRequest.getName();
        a.accountType = createBankAccountRequest.getAccountType();
        a.ownerid = sessionUser.getId();

        databaseService.saveAccount(a);

        BankAccountResponse.AccountTypeEnum type = BankAccountResponse.AccountTypeEnum.fromValue(a.accountType.getValue());
        BankAccountResponse resp = new BankAccountResponse(a.accountNumber, a.sortCode, a.accountName, type, a.balance, a.currency, a.createdTimestamp, a.updatedTimestamp);
        return ResponseEntity.ok(resp);
    }
}
