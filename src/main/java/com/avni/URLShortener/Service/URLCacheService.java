package com.avni.URLShortener.Service;

import com.avni.URLShortener.Entity.URL;
import com.avni.URLShortener.Exceptions.ResourceNotFoundException;
import com.avni.URLShortener.Repository.URLRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class URLCacheService {
    private final URLRepo urlRepo;

    @Cacheable(value = "shortCodeToUrlMapping", key = "#shortCode")
    public URL getCachedUrl(String shortCode){
        return urlRepo.findByShortCodeAndIsActive(shortCode, true)
                .orElseThrow(() ->
                        new ResourceNotFoundException("URL not found for shortCode: "+ shortCode));
    }
}
