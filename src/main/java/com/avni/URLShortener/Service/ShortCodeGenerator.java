package com.avni.URLShortener.Service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ShortCodeGenerator {
    public String generateShortCode(){
        String uuid = UUID.randomUUID().toString();
        String generatedCode = uuid.replace("-","");
        return generatedCode.substring(0,6);
    }
}
