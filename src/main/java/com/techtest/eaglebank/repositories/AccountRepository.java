package com.techtest.eaglebank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techtest.eaglebank.entities.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{}
