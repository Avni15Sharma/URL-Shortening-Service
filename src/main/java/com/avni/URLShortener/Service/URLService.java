package com.avni.URLShortener.Service;

import com.avni.URLShortener.DTO.GetURLStatsDTO;
import com.avni.URLShortener.DTO.URLRequestDTO;
import com.avni.URLShortener.DTO.URLResponseDTO;
import com.avni.URLShortener.Entity.URL;
import com.avni.URLShortener.Exceptions.ResourceNotFoundException;
import com.avni.URLShortener.Repository.URLRepo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class URLService {

    private final ShortCodeGenerator shortCodeGenerator;
    private final ModelMapper modelMapper;
    private final URLRepo urlRepo;

    public URLService(ShortCodeGenerator shortCodeGenerator, ModelMapper modelMapper, URLRepo urlRepo) {
        this.shortCodeGenerator = shortCodeGenerator;
        this.modelMapper = modelMapper;
        this.urlRepo = urlRepo;
    }

    public URLResponseDTO createShortUrl(URLRequestDTO urlRequestDTO) {
        String shortCode = shortCodeGenerator.generateShortCode();
        URL url = URL.builder()
                .url(urlRequestDTO.getUrl())
                .shortCode(shortCode)
                .build();
        URL savedUrl = urlRepo.save(url);
        return modelMapper.map(savedUrl,URLResponseDTO.class);
    }

    public URLResponseDTO retrieveOriginalUrl(String shortCode) {
        URL originalUrl = urlRepo.findByShortCode(shortCode)
                          .orElseThrow(() ->
                               new ResourceNotFoundException("URL not found for shortCode: "+ shortCode));
        originalUrl.setAccessCount(originalUrl.getAccessCount() + 1);
        urlRepo.save(originalUrl);
        return modelMapper.map(originalUrl, URLResponseDTO.class);
    }

    public URLResponseDTO updateShortUrl(String shortCode, URLRequestDTO urlRequestDTO) {
        URL existingUrl = urlRepo.findByShortCode(shortCode)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("URL does not exist for shortCode: "+ shortCode));
        existingUrl.setUrl(urlRequestDTO.getUrl());
        URL savedUrl = urlRepo.save(existingUrl);
        return modelMapper.map(savedUrl, URLResponseDTO.class);
    }

    public void delete(String shortCode) {
        URL existingUrl = urlRepo.findByShortCode(shortCode)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("URL does not exist for shortCode: "+shortCode));
        urlRepo.delete(existingUrl);
    }

    public GetURLStatsDTO getUrlStats(String shortCode) {
        URL existingUrl = urlRepo.findByShortCode(shortCode)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("URL not found for shortCode: "+shortCode));
        return modelMapper.map(existingUrl, GetURLStatsDTO.class);
    }
}
