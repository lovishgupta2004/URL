package com.example.URL.Service;

import com.example.URL.Entity.URL;
import com.example.URL.Repository.URLRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class URLServiceTest {
    private final URLRepository urlRepository = mock(URLRepository.class);
    private final URLService urlService = new URLService(urlRepository);

    @Test
    void expiredShortUrlIsDeletedAndNotRedirected() {
        URL url = new URL();
        url.setCreatedAt(Date.from(Instant.now().minus(Duration.ofDays(2)).minusSeconds(1)));
        when(urlRepository.findByShortUrl("expired")).thenReturn(url);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> urlService.getOriginalURl("expired"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(urlRepository).delete(url);
    }

    @Test
    void cleanupUsesTwoDayExpiryCutoff() {
        Instant beforeCleanup = Instant.now();

        urlService.deleteExpiredUrls();

        Instant afterCleanup = Instant.now();
        org.mockito.ArgumentCaptor<Date> expiryTime = org.mockito.ArgumentCaptor.forClass(Date.class);
        verify(urlRepository).deleteByCreatedAtBefore(expiryTime.capture());
        Instant actualCutoff = expiryTime.getValue().toInstant();
        assertFalse(actualCutoff.isBefore(Date.from(beforeCleanup.minus(Duration.ofDays(2))).toInstant()));
        assertFalse(actualCutoff.isAfter(Date.from(afterCleanup.minus(Duration.ofDays(2))).toInstant()));
    }
}
