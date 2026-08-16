package com.avni.URLShortener.Service;

import com.avni.URLShortener.Entity.URL;
import com.avni.URLShortener.Exceptions.ResourceNotFoundException;
import com.avni.URLShortener.Repository.URLRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class URLCacheServiceTest {
    private URLRepo urlRepo;
    private URLCacheService urlCacheService;

    @BeforeEach
    void setup(){
        urlRepo = mock(URLRepo.class);
        urlCacheService = new URLCacheService(urlRepo);
    }

    @Test
    void getCachedUrl_ShouldReturnUrl(){

        URL url = URL.builder()
                .id(1L)
                .url("https://google.com")
                .shortCode("abc123")
                .isActive(true)
                .accessCount(5L)
                .build();

        when(urlRepo.findByShortCodeAndIsActive("abc123", true))
                .thenReturn(Optional.of(url));

        URL result = urlCacheService.getCachedUrl("abc123");

        assertEquals(url, result);

        verify(urlRepo).findByShortCodeAndIsActive("abc123", true);
    }
    @Test
    void getCachedUrl_ShouldThrowExceptionWhenUrlNotFound(){

        when(urlRepo.findByShortCodeAndIsActive("abc123", true))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exc = assertThrows(
                ResourceNotFoundException.class,
                () -> urlCacheService.getCachedUrl("abc123")
        );

        assertEquals(
                "URL not found for shortCode: abc123",
                exc.getMessage()
        );

        verify(urlRepo).findByShortCodeAndIsActive("abc123", true);
    }
}
