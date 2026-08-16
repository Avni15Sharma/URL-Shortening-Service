package com.avni.URLShortener.Repository;

import com.avni.URLShortener.Entity.URL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface URLRepo extends JpaRepository<URL,Long> {
    Optional<URL> findByShortCodeAndIsActive(String shortCode, boolean isActive);

    boolean existsByShortCode(String shortCode);
}
