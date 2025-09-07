package com.techtest.eaglebank.delegates;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import com.baeldung.openapi.api.V1Api;
import com.baeldung.openapi.api.V1ApiDelegate;
import com.baeldung.openapi.model.CreateTransactionRequest;
import com.baeldung.openapi.model.ListTransactionsResponse;
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
            return ResponseEntity.notFound().build();
        }

        if (a.ownerid != sessionUser.getId()) {
            return ResponseEntity.status(403).build();
        }

        if (t.transfactionType == TransactionResponse.TypeEnum.DEPOSIT) {
            a.balance += t.amount;
        } else {
            if (a.balance < t.amount) {
                return ResponseEntity.unprocessableEntity().build();
            }

            a.balance -= t.amount;
        }

        a = databaseService.saveAccount(a);
        t = databaseService.saveTransaction(t);

        TransactionResponse resp = new TransactionResponse(t.getTransactionId(), t.amount, t.currency, t.transfactionType, t.createdTimestamp);
        return ResponseEntity.ok(resp);
        
    }

    /**
     * GET /v1/accounts/{accountNumber}/transactions
     * List transactions
     *
     * @param accountNumber Account number of the bank account (required)
     * @return The list of transaction details (status code 200)
     *         or The request didn&#39;t supply all the necessary data (status code 400)
     *         or Access token is missing or invalid (status code 401)
     *         or The user is not allowed to access the transactions (status code 403)
     *         or Bank account was not found (status code 404)
     *         or An unexpected error occurred (status code 500)
     * @see V1Api#listAccountTransaction
     */
    @Override
    public ResponseEntity<ListTransactionsResponse> listAccountTransaction(String accountNumber) {
        User sessionUser = null;
        if (getRequest().isPresent()) {
            sessionUser = (User)getRequest().get().getAttribute("user", 0);
        }
        if (sessionUser == null) {
            return ResponseEntity.status(401).build();
        }

        Account a = databaseService.getAccount(accountNumber);
        if (a == null) {
            return ResponseEntity.notFound().build();
        }

        if (a.ownerid != sessionUser.getId()) {
            return ResponseEntity.status(403).build();
        }

        List<Transaction> transactions = databaseService.getTransactionsForAccount(a);

        ListTransactionsResponse resp = new ListTransactionsResponse();
        for (Transaction t : transactions) {
            TransactionResponse tr = new TransactionResponse(t.getTransactionId(), t.amount, t.currency, t.transfactionType, t.createdTimestamp);
            tr.setReference(t.reference);
            tr.setUserId(sessionUser.userid);
            resp.addTransactionsItem(tr);
        }
        return ResponseEntity.ok(resp);
    }

     /**
     * GET /v1/accounts/{accountNumber}/transactions/{transactionId}
     * Fetch transaction by ID.
     *
     * @param accountNumber Account number of the bank account (required)
     * @param transactionId ID of the transaction (required)
     * @return The transaction details (status code 200)
     *         or The request didn&#39;t supply all the necessary data (status code 400)
     *         or Access token is missing or invalid (status code 401)
     *         or The user is not allowed to access the transaction (status code 403)
     *         or Bank account was not found (status code 404)
     *         or An unexpected error occurred (status code 500)
     * @see V1Api#fetchAccountTransactionByID
     */
    @Override
    public ResponseEntity<TransactionResponse> fetchAccountTransactionByID(String accountNumber, String transactionId) {
        User sessionUser = null;
        if (getRequest().isPresent()) {
            sessionUser = (User)getRequest().get().getAttribute("user", 0);
        }
        if (sessionUser == null) {
            return ResponseEntity.status(401).build();
        }

        Account a = databaseService.getAccount(accountNumber);
        if (a == null) {
            return ResponseEntity.notFound().build();
        }

        if (a.ownerid != sessionUser.getId()) {
            return ResponseEntity.status(403).build();
        }

        Transaction t = databaseService.getTransactionFromTransactionId(transactionId);
        if (t == null) {
            System.out.println("transaction not found");
            return ResponseEntity.notFound().build();
        }
        if (t.accountNumber.compareTo(a.accountNumber) != 0) {
            return ResponseEntity.notFound().build();
        }

        TransactionResponse tr = new TransactionResponse(t.getTransactionId(), t.amount, t.currency, t.transfactionType, t.createdTimestamp);
        tr.setReference(t.reference);
        tr.setUserId(sessionUser.userid);
        
        return ResponseEntity.ok(tr);
    }
}
