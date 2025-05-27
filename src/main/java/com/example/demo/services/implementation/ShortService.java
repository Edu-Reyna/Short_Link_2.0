package com.example.demo.services.implementation;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class ShortService {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom random = new SecureRandom();

    public String generateShortUrl(int length) {
        StringBuilder shortUrl = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(BASE62.length());
            shortUrl.append(BASE62.charAt(index));
        }
        return shortUrl.toString();
    }
}
