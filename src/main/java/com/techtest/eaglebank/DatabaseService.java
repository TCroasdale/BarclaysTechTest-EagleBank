package com.techtest.eaglebank;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techtest.eaglebank.entities.Account;
import com.techtest.eaglebank.entities.Session;
import com.techtest.eaglebank.entities.Transaction;
import com.techtest.eaglebank.entities.User;
import com.techtest.eaglebank.repositories.AccountRepository;
import com.techtest.eaglebank.repositories.SessionRepository;
import com.techtest.eaglebank.repositories.TransactionRepository;
import com.techtest.eaglebank.repositories.UserRepository;

@Service
@Transactional
public class DatabaseService {
    @Autowired UserRepository userRepository;
    @Autowired SessionRepository sessionRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired TransactionRepository transactionRepository;

    public User saveUser(User u) {
        return userRepository.save(u);
    }

    public User getUser(Long id) {
        return userRepository.getReferenceById(id);
    }

    public User getUserFromUserID(String userid) {
        return userRepository.findByUserid(userid);
    }

    public long getUserCount() {
        return userRepository.count();
    }

    public Session createSession(Session s) {
        return sessionRepository.save(s);
    }

    public boolean checkSessionExists(Session s) {
        try {
            Session s2 = sessionRepository.getReferenceById(s.getId());
            return s2 != null;
        } catch(Exception e) {
            return false;
        }
    }

    public Account saveAccount(Account a) {
        return accountRepository.save(a);
    }

    public Account getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public Transaction saveTransaction(Transaction t) {
        return transactionRepository.save(t);
    }

    public List<Transaction> getTransactionsForAccount(Account a) {
        return transactionRepository.findByAccountNumber(a.accountNumber);
    }

    public void Reset() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();
        sessionRepository.deleteAll();
    }

}
