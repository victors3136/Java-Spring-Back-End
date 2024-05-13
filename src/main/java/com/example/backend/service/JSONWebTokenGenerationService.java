package com.example.backend.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Service
public class JSONWebTokenGenerationService {
    @Value("${jwt.key}")
    private String KEY;

    public String generateToken(String username) {
        Key key = new SecretKeySpec(KEY.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        return Jwts.builder()
                .setSubject(username)
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
