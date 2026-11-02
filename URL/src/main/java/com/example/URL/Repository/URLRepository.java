package com.example.URL.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.Date;
import com.example.URL.Entity.URL;

@Repository
public interface URLRepository extends JpaRepository<URL, UUID> {
    boolean existsByShortUrl(String shortUrl);
    URL findByOriginalUrl(String OriginalUrl);
    URL findByShortUrl(String ShortUrl);

    @Transactional
    void deleteByCreatedAtBefore(Date expiryTime);
}
