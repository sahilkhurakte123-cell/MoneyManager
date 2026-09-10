package com.example.MoneyManager.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginAttemptService {

    private final static int MAX_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MS = 15*60*1000; // 15 minutes
    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();
    private final Map<String, Long> lockedUntil = new ConcurrentHashMap<>();

    public boolean isBlocked(String email) {
        Long until =  lockedUntil.get(email);
        if(until == null){
            return false;
        }
        if(System.currentTimeMillis() > until){
            lockedUntil.remove(email);
            attempts.remove(email);
            return false;
        }
        return true;
    }

    public void loginFailed(String email) {
        int count = attempts.merge(email, 1, Integer::sum);
        if (count >= MAX_ATTEMPTS) {
            lockedUntil.put(email, System.currentTimeMillis() + LOCK_DURATION_MS);
        }
    }

    public void loginSucceeded(String email) {
        attempts.remove(email);
        lockedUntil.remove(email);
    }

}
