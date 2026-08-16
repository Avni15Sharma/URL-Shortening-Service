package com.avni.URLShortener.Config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.modelmapper.ModelMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class AppConfig {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public Caffeine caffeineConfig(){
        return Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.HOURS)
                .recordStats();
    }

    @Bean
    public CacheManager cacheManager(Caffeine caffeine){
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }
}
