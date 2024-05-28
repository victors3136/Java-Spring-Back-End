package com.example.backend.service;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenTimespanService {
    private static final long tokenLifetimeInMillis
            /*  1_000   milliseconds/second
            *     60   seconds/minute
            *     60   minutes/hour
            *     10   hours
            _________________________________ */
            = 36_000_000 /* milliseconds */;
    public static Date currentTime(){
        return new Date();
    }
    public static Date buildIssueDate(){
        return currentTime();
    }
    public static Date buildExpirationDate(){
        return new Date(System.currentTimeMillis() + tokenLifetimeInMillis);
    }
}
