package com.avni.URLShortener.Controller;

import com.avni.URLShortener.DTO.GetURLStatsDTO;
import com.avni.URLShortener.DTO.URLRequestDTO;
import com.avni.URLShortener.DTO.URLResponseDTO;
import com.avni.URLShortener.Service.URLService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shorten")
@RequiredArgsConstructor
public class URLController {

    private final URLService urlService;

    @PostMapping()
    public ResponseEntity<URLResponseDTO> createShortUrl(@Valid @RequestBody URLRequestDTO urlRequestDTO){
        URLResponseDTO shortUrl = urlService.createShortUrl(urlRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(shortUrl);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<URLResponseDTO> retrieveOriginalUrl(@PathVariable String shortCode){
        return ResponseEntity.ok(urlService.retrieveOriginalUrl(shortCode));
    }

    @PutMapping("/{shortCode}")
    public ResponseEntity<URLResponseDTO> updateShortUrl(@PathVariable String shortCode, @Valid @RequestBody URLRequestDTO urlRequestDTO){
        return ResponseEntity.ok(urlService.updateShortUrl(shortCode,urlRequestDTO));
    }

    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> delete(@PathVariable String shortCode){
        urlService.delete(shortCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{shortCode}/stats")
    public ResponseEntity<GetURLStatsDTO> getUrlStats(@PathVariable String shortCode){
        return ResponseEntity.ok(urlService.getUrlStats(shortCode));
    }
}
