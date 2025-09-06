package com.techtest.eaglebank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techtest.eaglebank.entities.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long>{}
