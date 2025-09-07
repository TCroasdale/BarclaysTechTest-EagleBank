package com.techtest.eaglebank.delegates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.baeldung.openapi.api.V1ApiDelegate;
import com.baeldung.openapi.model.AuthorizationRequest;
import com.baeldung.openapi.model.AuthorizationResponse;
import com.baeldung.openapi.model.BankAccountResponse;
import com.baeldung.openapi.model.CreateBankAccountRequest;
import com.baeldung.openapi.model.CreateTransactionRequest;
import com.baeldung.openapi.model.CreateUserRequest;
import com.baeldung.openapi.model.ListTransactionsResponse;
import com.baeldung.openapi.model.TransactionResponse;
import com.baeldung.openapi.model.UserResponse;

@Import({ AuthDelegate.class, UserDelegate.class, AccountDelegate.class, TransactionDelegate.class })
@Component
public class ApiDelegate implements V1ApiDelegate {
    @Autowired AuthDelegate authDelegate;
    @Autowired UserDelegate userDelegate;
    @Autowired AccountDelegate accountDelegate;
    @Autowired TransactionDelegate transactionDelegate;

    public ResponseEntity<UserResponse> createUser(CreateUserRequest cur) {
        return userDelegate.createUser(cur);
    }

    public ResponseEntity<AuthorizationResponse> authorize(AuthorizationRequest ar) {
        return authDelegate.authorize(ar);
    }

    public ResponseEntity<UserResponse> fetchUserByID(String userId) {
        return userDelegate.fetchUserByID(userId);
    }

    public ResponseEntity<BankAccountResponse> createAccount(CreateBankAccountRequest createBankAccountRequest) {
        return accountDelegate.createAccount(createBankAccountRequest);
    }


    public ResponseEntity<TransactionResponse> createTransaction(String accountNumber, CreateTransactionRequest createTransactionRequest) {
        return transactionDelegate.createTransaction(accountNumber, createTransactionRequest);
    }

    public ResponseEntity<ListTransactionsResponse> listAccountTransaction(String accountNumber) {
        return transactionDelegate.listAccountTransaction(accountNumber);
    }

    public ResponseEntity<TransactionResponse> fetchAccountTransactionByID(String accountNumber, String transactionId) {
        return transactionDelegate.fetchAccountTransactionByID(accountNumber, transactionId);
    }

    public ResponseEntity<BankAccountResponse> fetchAccountByAccountNumber(String accountNumber) {
        return accountDelegate.fetchAccountByAccountNumber(accountNumber);
    }
}
