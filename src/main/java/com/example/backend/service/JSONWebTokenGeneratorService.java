package com.example.backend.service;

import com.example.backend.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
public class JSONWebTokenGeneratorService {

    private final String hashingKey;

    public JSONWebTokenGeneratorService(@Value("${jwt.key}") String hashingKey) {
        this.hashingKey = hashingKey;
    }

    public String generateJWT(User user) {
        UUID userID = user.getId();
        Key key = new SecretKeySpec(hashingKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        return Jwts.builder()
                .setSubject(userID.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()
                        /*  1_000   milliseconds/second
                         *     60   seconds/minute
                         *     60   minutes/hour
                         *     10   hours
                         _________________________________ */
                        /*=*/ + 36_000_000 /* milliseconds */))
                .signWith(key)
                .compact();
    }
}