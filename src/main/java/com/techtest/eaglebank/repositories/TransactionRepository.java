package com.techtest.eaglebank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techtest.eaglebank.entities.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{}
