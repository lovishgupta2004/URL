package com.example.URL.Controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.example.URL.Service.URLService;

@RestController 
@RequestMapping ("url")
@CrossOrigin(origins = "http://localhost:5173")
public class URLController {
    private final URLService URLService;

    public URLController(URLService URLService) {
        this.URLService = URLService;
    }

    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
    public String shortenURL(@RequestBody String originalUrl) {
        return URLService.ShortenURL(originalUrl);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public String shortenURL(@RequestBody ShortenURLRequest request) {
        if (request.customUrl() == null || request.customUrl().isBlank()) {
            return URLService.ShortenURL(request.originalUrl());
        }
        return URLService.ShortenURLCustom(request.originalUrl(), request.customUrl());
    }

    @GetMapping("/{shorturl}")
    public ResponseEntity<Void> redirect(@PathVariable String shorturl){
        String originalurl=URLService.getOriginalURl(shorturl);
        return ResponseEntity.status(HttpStatus.FOUND).
        location(URI.create(originalurl)).build();
    }

    public record ShortenURLRequest(String originalUrl, String customUrl) {}
}
