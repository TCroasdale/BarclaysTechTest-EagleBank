package com.techtest.eaglebank;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techtest.eaglebank.entities.User;
import com.techtest.eaglebank.repositories.UserRepository;

@Service
public class DatabaseService {
    @Autowired UserRepository userRepository;

    public User saveUser(User u) {
        if (u.createdTimestamp == null) {
            u.createdTimestamp = OffsetDateTime.now();
        }
        u.updatedTimestamp = OffsetDateTime.now();
        return userRepository.save(u);
    }

    public long getUserCount() {
        return userRepository.count();
    }

}
