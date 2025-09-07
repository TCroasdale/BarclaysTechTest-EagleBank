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
        a.accountType = BankAccountResponse.AccountTypeEnum.fromValue(createBankAccountRequest.getAccountType().getValue());
        a.ownerid = sessionUser.getId();

        databaseService.saveAccount(a);

        BankAccountResponse resp = new BankAccountResponse(a.accountNumber, a.sortCode, a.accountName, a.accountType, a.balance, a.currency, a.createdTimestamp, a.updatedTimestamp);
        return ResponseEntity.ok(resp);
    }

    /**
     * GET /v1/accounts/{accountNumber}
     * Fetch account by account number.
     *
     * @param accountNumber Account number of the bank account (required)
     * @return The bank account details (status code 200)
     *         or The request didn&#39;t supply all the necessary data (status code 400)
     *         or The user was not authenticated (status code 401)
     *         or The user is not allowed to access the bank account details (status code 403)
     *         or Bank account was not found (status code 404)
     *         or An unexpected error occurred (status code 500)
     * @see V1Api#fetchAccountByAccountNumber
     */
    @Override
    public ResponseEntity<BankAccountResponse> fetchAccountByAccountNumber(String accountNumber) {
        User sessionUser = null;
        if (getRequest().isPresent()) {
            sessionUser = (User)getRequest().get().getAttribute("user", 0);
        }
        if (sessionUser == null) {
            return ResponseEntity.status(401).build();
        }

        Account account = databaseService.getAccount(accountNumber);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }

        if (account.ownerid != sessionUser.getId()) {
            return ResponseEntity.status(403).build();
        }

        BankAccountResponse resp = new BankAccountResponse(account.accountNumber, account.sortCode, account.accountName, account.accountType, account.balance, account.currency, account.createdTimestamp, account.updatedTimestamp);
        return ResponseEntity.ok(resp);
    }
}
