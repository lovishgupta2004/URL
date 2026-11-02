package com.example.URL.Service;

import com.example.URL.Repository.URLRepository;
import com.example.URL.Entity.URL;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class URLService {
    private final URLRepository urlRepository;

    public URLService(URLRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    private static final String characters="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Duration URL_TTL = Duration.ofDays(1);

    public String generateCode(){
        StringBuilder sb=new StringBuilder();
        for(int i=0; i<6; i++){
            int index=(int)(Math.random()*characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    public String generateUniqueCode(){
        String code=generateCode();
        while(urlRepository.existsByShortUrl(code)){
            code=generateCode();
        }
        return code;
    }

    public String ShortenURL(String originalUrl){
        URL check=urlRepository.findByOriginalUrl(originalUrl);
        if(check!=null){
            if (!deleteIfExpired(check)) {
                return check.getShortUrl();
            }
        }
        URL URL=new URL();
        String ShortCode=generateUniqueCode();
        URL.setOriginalUrl(originalUrl);
        URL.setShortUrl(ShortCode);
        urlRepository.save(URL);
        return ShortCode;
    }

    public String ShortenURLCustom(String originalUrl, String CustomUrl){
        URL existing = urlRepository.findByShortUrl(CustomUrl);
        if (existing != null && !deleteIfExpired(existing)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This custom URL is already taken");
        }

        URL URL=new URL();
        String ShortCode=CustomUrl;
        URL.setOriginalUrl(originalUrl);
        URL.setShortUrl(ShortCode);
        urlRepository.save(URL);
        return ShortCode;
    }

    public String getOriginalURl(String shorturl){
        URL url=urlRepository.findByShortUrl(shorturl);
        if(url==null || deleteIfExpired(url)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No ShortUrl Found");
        }
        return url.getOriginalUrl();
    }

    @Scheduled(fixedDelay = 3_600_000)
    public void deleteExpiredUrls() {
        Date expiryTime = Date.from(Instant.now().minus(URL_TTL));
        urlRepository.deleteByCreatedAtBefore(expiryTime);
    }

    private boolean deleteIfExpired(URL url) {
        Date expiryTime = Date.from(Instant.now().minus(URL_TTL));
        if (url.getCreatedAt() != null && !url.getCreatedAt().after(expiryTime)) {
            urlRepository.delete(url);
            return true;
        }
        return false;
    }
}
