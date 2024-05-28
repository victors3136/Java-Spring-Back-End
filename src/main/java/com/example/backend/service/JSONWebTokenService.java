package com.example.backend.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JSONWebTokenService {

    private final Key key;
    private final JwtParser parser;
    private final JwtBuilder builder;
    private final Function<String, Claims> decodeTokenBody;
    private final Function<UUID, String> encodeTokenBody;

    private String stripBearer(String token) {
        return token.replace("Bearer ", "");
    }

    public JSONWebTokenService(@Value("${jwt.key}") String hashingKey) {
        byte[] keyBytes = hashingKey.getBytes();
        this.key = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
        this.parser = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(keyBytes))
                .build();
        this.builder = Jwts.builder();
        this.decodeTokenBody = (String token) -> (Claims) parser.parse(stripBearer(token)).getBody();
        this.encodeTokenBody = (UUID id) -> builder.setSubject(id.toString())
                .setIssuedAt(TokenTimespanService.buildIssueDate())
                .setExpiration(TokenTimespanService.buildExpirationDate())
                .signWith(key)
                .compact();
    }


    public String encode(UUID userID) throws JwtException {
        return this.encodeTokenBody.apply(userID);
    }

    public UUID decode(String token) throws JwtException {
        return UUID.fromString(decodeTokenBody.apply(token).getSubject());
    }

    public boolean hasExpired(String token) {
        return decodeTokenBody.apply(token)
                .getExpiration()
                .before(TokenTimespanService.currentTime());
    }

    public UUID parse(String token) {
        return decode(stripBearer(token));
    }
}