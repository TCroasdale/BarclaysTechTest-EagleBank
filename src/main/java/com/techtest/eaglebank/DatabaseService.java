package com.techtest.eaglebank;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techtest.eaglebank.entities.Session;
import com.techtest.eaglebank.entities.User;
import com.techtest.eaglebank.repositories.SessionRepository;
import com.techtest.eaglebank.repositories.UserRepository;

import jakarta.validation.constraints.Null;

@Service
@Transactional
public class DatabaseService {
    @Autowired UserRepository userRepository;
    @Autowired SessionRepository sessionRepository;

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

}
