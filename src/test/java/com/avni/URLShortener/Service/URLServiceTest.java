package com.avni.URLShortener.Service;

import com.avni.URLShortener.DTO.GetURLStatsDTO;
import com.avni.URLShortener.DTO.URLRequestDTO;
import com.avni.URLShortener.DTO.URLResponseDTO;
import com.avni.URLShortener.Entity.URL;
import com.avni.URLShortener.Exceptions.ResourceNotFoundException;
import com.avni.URLShortener.Repository.URLRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class URLServiceTest {
    private ShortCodeGenerator shortCodeGenerator;
    private ModelMapper modelMapper;
    private URLRepo urlRepo;
    private URLCacheService urlCacheService;
    private URLService urlService;

    @BeforeEach
    void setup(){
        shortCodeGenerator = mock(ShortCodeGenerator.class);
        modelMapper = new ModelMapper();
        urlRepo = mock(URLRepo.class);
        urlCacheService = mock(URLCacheService.class);

        urlService = new URLService(
                shortCodeGenerator,
                modelMapper,
                urlRepo,
                urlCacheService
        );
    }

    @Test
    void createShortUrl_shouldCreateAndReturnShortUrl(){
        URLRequestDTO urlRequestDTO = new URLRequestDTO();
        urlRequestDTO.setUrl("https://google.com");

        when(shortCodeGenerator.generateShortCode()).thenReturn("abc123");
        when(urlRepo.existsByShortCode("abc123")).thenReturn(false);

        URL savedUrl = URL.builder()
                .id(1L)
                .url(urlRequestDTO.getUrl())
                .shortCode("abc123")
                .isActive(true)
                .build();
        when((urlRepo.save(any(URL.class)))).thenReturn(savedUrl);

        URLResponseDTO urlResponseDTO = urlService.createShortUrl(urlRequestDTO);

        assertEquals("https://google.com", urlResponseDTO.getUrl());
        assertEquals("abc123", urlResponseDTO.getShortCode());
        assertEquals(1L,urlResponseDTO.getId());

        verify(urlRepo).save(any(URL.class));
    }

    @Test
    void createShortUrl_ShouldGenerateNewCodeWhenCodeAlreadyExists(){
        URLRequestDTO urlRequestDTO = new URLRequestDTO();
        urlRequestDTO.setUrl("https://google.com");

        when(shortCodeGenerator.generateShortCode()).thenReturn("abc123","xyz789");
        when(urlRepo.existsByShortCode("abc123")).thenReturn(true);
        when(urlRepo.existsByShortCode("xyz789")).thenReturn(false);

        URL savedUrl = URL.builder()
                .id(1L)
                .url("https://google.com")
                .isActive(true)
                .shortCode("xyz789")
                .build();
        when(urlRepo.save(any(URL.class))).thenReturn(savedUrl);

        URLResponseDTO urlResponseDTO = urlService.createShortUrl(urlRequestDTO);
        assertEquals("xyz789", urlResponseDTO.getShortCode());
        verify(shortCodeGenerator,times(2)).generateShortCode();
        verify(urlRepo).existsByShortCode("abc123");
        verify(urlRepo).existsByShortCode("xyz789");
        verify(urlRepo).save(any(URL.class));
    }

    @Test
    void retrieveOriginalUrl_ShouldReturnUrlAndIncreaseAccessCount(){
        URL url = URL.builder()
                        .id(1L)
                        .url("https://google.com")
                        .shortCode("abc123")
                        .isActive(true)
                        .accessCount(0L)
                        .build();

        when(urlCacheService.getCachedUrl("abc123")).thenReturn(url);

        URLResponseDTO urlResponseDTO = urlService.retrieveOriginalUrl("abc123");

        assertEquals("https://google.com",urlResponseDTO.getUrl());
        assertEquals("abc123",urlResponseDTO.getShortCode());
        assertEquals(1L,urlResponseDTO.getId());
        assertEquals(1L,url.getAccessCount());

        verify(urlCacheService).getCachedUrl("abc123");
        verify(urlRepo).save(any(URL.class));
    }

    @Test
    void retrieveOriginalUrl_ShouldThrowExceptionWhenUrlNotFound(){
        when(urlCacheService.getCachedUrl("abc123"))
                .thenThrow(
                        new ResourceNotFoundException("URL not found for shortCode: "+ "abc123"));

        ResourceNotFoundException exc = assertThrows(ResourceNotFoundException.class,
                () -> urlService.retrieveOriginalUrl("abc123"));

        assertEquals("URL not found for shortCode: "+ "abc123",exc.getMessage());

        verify(urlCacheService).getCachedUrl("abc123");
        verify(urlRepo,never()).save(any(URL.class));
    }

    @Test
    void updateShortUrl_ShouldUpdateAndReturnUrl(){
        URLRequestDTO urlRequestDTO = new URLRequestDTO();
        urlRequestDTO.setUrl("https://google.com");

        URL savedUrl = URL.builder()
                .id(1L)
                .url("https://amazon.com")
                .shortCode("abc123")
                .isActive(true)
                .build();
        URL updatedUrl = URL.builder()
                .id(1L)
                .url("https://google.com")
                .shortCode("abc123")
                .isActive(true)
                .build();

        when(urlRepo.findByShortCodeAndIsActive("abc123",true))
                .thenReturn(Optional.of(savedUrl));

        when(urlRepo.save(any(URL.class))).thenReturn(updatedUrl);

        URLResponseDTO urlResponseDTO = urlService.updateShortUrl("abc123",urlRequestDTO);

        assertEquals("https://google.com",urlResponseDTO.getUrl());
        verify(urlRepo).findByShortCodeAndIsActive("abc123",true);
        verify(urlRepo).save(any(URL.class));
    }
    @Test
    void updateShortUrl_ShouldThrowExceptionWhenUrlNotFound(){
        URLRequestDTO urlRequestDTO = new URLRequestDTO();
        urlRequestDTO.setUrl("https://google.com");

        when(urlRepo.findByShortCodeAndIsActive("abc123",true))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exc = assertThrows(ResourceNotFoundException.class,
                () -> urlService.updateShortUrl("abc123", urlRequestDTO));

        verify(urlRepo).findByShortCodeAndIsActive("abc123",true);
        verify(urlRepo,never()).save(any(URL.class));
        assertEquals("URL does not exist for shortCode: "+ "abc123", exc.getMessage());
    }

    @Test
    void delete_ShouldDeactivateUrl(){
        URL savedUrl = URL.builder()
                .id(1L)
                .url("https://google.com")
                .shortCode("abc123")
                .isActive(true)
                .build();
        when(urlRepo.findByShortCodeAndIsActive("abc123",true))
                .thenReturn(Optional.of(savedUrl));

        urlService.delete("abc123");

        assertFalse(savedUrl.isActive());
        verify(urlRepo).findByShortCodeAndIsActive("abc123",true);
        verify(urlRepo).save(any(URL.class));
    }
    @Test
    void delete_ShouldThrowExceptionWhenUrlNotFound(){
        when(urlRepo.findByShortCodeAndIsActive("abc123",true))
                .thenReturn(Optional.empty());
        ResourceNotFoundException exc = assertThrows(ResourceNotFoundException.class,
                () -> urlService.delete("abc123"));

        verify(urlRepo).findByShortCodeAndIsActive("abc123",true);
        verify(urlRepo,never()).save(any(URL.class));
        assertEquals(
                "URL does not exist for shortCode: "+ "abc123",
                exc.getMessage()
        );
    }

    @Test
    void getUrlStats_ShouldReturnUrlStats(){
        URL savedUrl = URL.builder()
                .id(1L)
                .url("https://google.com")
                .shortCode("abc123")
                .isActive(true)
                .accessCount(10L)
                .build();
        when(urlRepo.findByShortCodeAndIsActive("abc123",true))
                .thenReturn(Optional.of(savedUrl));

        GetURLStatsDTO getURLStatsDTO = urlService.getUrlStats("abc123");

        assertEquals(1L,getURLStatsDTO.getId());
        assertEquals("https://google.com",getURLStatsDTO.getUrl());
        assertEquals("abc123",getURLStatsDTO.getShortCode());
        assertEquals(10L,getURLStatsDTO.getAccessCount());
        verify(urlRepo).findByShortCodeAndIsActive("abc123",true);

    }
    @Test
    void getUrlStats_ShouldThrowExceptionWhenUrlNotFound(){
        when(urlRepo.findByShortCodeAndIsActive("abc123",true))
                .thenReturn(Optional.empty());
        ResourceNotFoundException exc = assertThrows(ResourceNotFoundException.class,
                () -> urlService.getUrlStats("abc123"));

        verify(urlRepo).findByShortCodeAndIsActive("abc123",true);
        assertEquals(
                "URL not found for shortCode: "+ "abc123",
                exc.getMessage()
        );
    }

}
