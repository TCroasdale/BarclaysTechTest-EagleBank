package com.techtest.eaglebank.delegates;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import com.baeldung.openapi.api.V1Api;
import com.baeldung.openapi.api.V1ApiDelegate;
import com.baeldung.openapi.model.CreateTransactionRequest;
import com.baeldung.openapi.model.TransactionResponse;
import com.techtest.eaglebank.DatabaseService;
import com.techtest.eaglebank.entities.Account;
import com.techtest.eaglebank.entities.Transaction;
import com.techtest.eaglebank.entities.User;

public class TransactionDelegate implements V1ApiDelegate {
    
    @Autowired
    DatabaseService databaseService;

    @Autowired NativeWebRequest nativeWebRequest;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.of(nativeWebRequest);
    }
    
       /**
     * POST /v1/accounts/{accountNumber}/transactions
     * Create a transaction
     *
     * @param accountNumber Account number of the bank account (required)
     * @param createTransactionRequest Create a new transaction (required)
     * @return Transaction has been created successfully (status code 201)
     *         or Invalid details supplied (status code 400)
     *         or Access token is missing or invalid (status code 401)
     *         or The user is not allowed to delete the bank account details (status code 403)
     *         or Bank account was not found (status code 404)
     *         or Insufficient funds to process transaction (status code 422)
     *         or An unexpected error occurred (status code 500)
     * @see V1Api#createTransaction
     */
    @Override
    public ResponseEntity<TransactionResponse> createTransaction(String accountNumber, CreateTransactionRequest createTransactionRequest) {
        User sessionUser = null;
        if (getRequest().isPresent()) {
            sessionUser = (User)getRequest().get().getAttribute("user", 0);
        }
        if (sessionUser == null) {
            System.out.println("No user session.");
            return ResponseEntity.status(401).build();
        }

        Transaction t = new Transaction();
        t.amount = createTransactionRequest.getAmount();
        t.reference = createTransactionRequest.getReference();
        t.transfactionType = TransactionResponse.TypeEnum.fromValue(createTransactionRequest.getType().getValue());
        t.accountNumber = accountNumber;
        t.currency = TransactionResponse.CurrencyEnum.fromValue(createTransactionRequest.getCurrency().getValue());

        Account a = databaseService.getAccount(accountNumber);
        if (a == null) {
            System.out.println("Couldn't find account");
            return ResponseEntity.notFound().build();
        }

        if (a.ownerid != sessionUser.getId()) {
            System.out.println("Not the logged in users' account");
            return ResponseEntity.status(403).build();
        }

        if (t.transfactionType == TransactionResponse.TypeEnum.DEPOSIT) {
            a.balance += t.amount;
        } else {
            if (a.balance < t.amount) {
                System.out.println("Insufficient funds");
                return ResponseEntity.unprocessableEntity().build();
            }

            a.balance -= t.amount;
        }

        a = databaseService.saveAccount(a);
        t = databaseService.saveTransaction(t);

        TransactionResponse resp = new TransactionResponse(Long.toString(t.getId()), t.amount, t.currency, t.transfactionType, t.createdTimestamp);
        return ResponseEntity.ok(resp);
        
    }
}
