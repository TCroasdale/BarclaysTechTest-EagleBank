package com.techtest.eaglebank;

import java.time.OffsetDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techtest.eaglebank.entities.Session;
import com.techtest.eaglebank.entities.User;

@Service
public class JwtService {
    @Autowired DatabaseService dbService;

    public String IssueToken(User u) throws Exception{
        Session s = new Session();
        s.userid = u.userid;
        dbService.createSession(s);

        // Encode to JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(s);

        return Base64.getUrlEncoder().encodeToString(json.getBytes());
    }

    public Session DecodeToken(String token) throws Exception {
        byte[] json = Base64.getUrlDecoder().decode(token);
        ObjectMapper mapper = new ObjectMapper();
        Session s = mapper.readValue(json, Session.class );
        return s;
    }

    public boolean CheckToken(Session s) throws Exception{
        return dbService.checkSessionExists(s);
    }

}
