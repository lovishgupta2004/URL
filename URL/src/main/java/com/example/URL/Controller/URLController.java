package com.example.URL.Controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.URL.Service.URLService;

import lombok.AllArgsConstructor;

@RestController 
@RequestMapping ("url")
@AllArgsConstructor 
public class URLController {
    private final URLService URLService;

    @PostMapping
    public String shortenURL(@RequestBody String originalUrl) {
        return URLService.ShortenURL(originalUrl);
    }

    @GetMapping("/{shorturl}")
    public ResponseEntity<Void> redirect(@PathVariable String shorturl){
        String originalurl=URLService.getOriginalURl(shorturl);
        return ResponseEntity.status(HttpStatus.FOUND).
        location(URI.create(originalurl)).build();
    }
}
