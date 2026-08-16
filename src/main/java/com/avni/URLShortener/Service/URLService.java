    package com.avni.URLShortener.Service;

    import com.avni.URLShortener.DTO.GetURLStatsDTO;
    import com.avni.URLShortener.DTO.URLRequestDTO;
    import com.avni.URLShortener.DTO.URLResponseDTO;
    import com.avni.URLShortener.Entity.URL;
    import com.avni.URLShortener.Exceptions.ResourceNotFoundException;
    import com.avni.URLShortener.Repository.URLRepo;
    import org.modelmapper.ModelMapper;
    import org.springframework.cache.annotation.CacheEvict;
    import org.springframework.stereotype.Service;

    @Service
    public class URLService {

        private final ShortCodeGenerator shortCodeGenerator;
        private final ModelMapper modelMapper;
        private final URLRepo urlRepo;
        private final URLCacheService urlCacheService;

        public URLService(ShortCodeGenerator shortCodeGenerator, ModelMapper modelMapper, URLRepo urlRepo, URLCacheService urlCacheService) {
            this.shortCodeGenerator = shortCodeGenerator;
            this.modelMapper = modelMapper;
            this.urlRepo = urlRepo;
            this.urlCacheService = urlCacheService;
        }

        public URLResponseDTO createShortUrl(URLRequestDTO urlRequestDTO) {
            boolean isCodeUnique = false;
            String shortCode = "";
            while(!isCodeUnique){
                shortCode = shortCodeGenerator.generateShortCode();
                isCodeUnique = !urlRepo.existsByShortCode(shortCode);
            }
            URL url = URL.builder()
                    .url(urlRequestDTO.getUrl())
                    .shortCode(shortCode)
                    .isActive(true)
                    .build();
            URL savedUrl = urlRepo.save(url);
            return modelMapper.map(savedUrl,URLResponseDTO.class);
        }

        public URLResponseDTO retrieveOriginalUrl(String shortCode) {
            URL originalUrl = urlCacheService.getCachedUrl(shortCode);
            originalUrl.setAccessCount(originalUrl.getAccessCount() + 1);
            urlRepo.save(originalUrl);
            return modelMapper.map(originalUrl, URLResponseDTO.class);
        }

        @CacheEvict(value = "shortCodeToUrlMapping", key = "#shortCode")
        public URLResponseDTO updateShortUrl(String shortCode, URLRequestDTO urlRequestDTO) {
            URL existingUrl = urlRepo.findByShortCodeAndIsActive(shortCode, true)
                                .orElseThrow(() ->
                                        new ResourceNotFoundException("URL does not exist for shortCode: "+ shortCode));
            existingUrl.setUrl(urlRequestDTO.getUrl());
            URL savedUrl = urlRepo.save(existingUrl);
            return modelMapper.map(savedUrl, URLResponseDTO.class);
        }

        @CacheEvict(value = "shortCodeToUrlMapping", key = "#shortCode")
        public void delete(String shortCode) {
            URL existingUrl = urlRepo.findByShortCodeAndIsActive(shortCode, true)
                                .orElseThrow(() ->
                                        new ResourceNotFoundException("URL does not exist for shortCode: "+shortCode));
            existingUrl.setActive(false);
            urlRepo.save(existingUrl);
        }

        public GetURLStatsDTO getUrlStats(String shortCode) {
            URL existingUrl = urlRepo.findByShortCodeAndIsActive(shortCode, true)
                                .orElseThrow(() ->
                                        new ResourceNotFoundException("URL not found for shortCode: "+shortCode));
            return modelMapper.map(existingUrl, GetURLStatsDTO.class);
        }
    }
