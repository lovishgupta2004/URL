package com.example.URL.Service;

import com.example.URL.Repository.URLRepository;
import com.example.URL.Entity.URL;


import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class URLService {
    private final URLRepository urlRepository;

    private static final String characters="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

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
            return check.getShortUrl();
        }
        URL URL=new URL();
        String ShortCode=generateUniqueCode();
        URL.setOriginalUrl(originalUrl);
        URL.setShortUrl(ShortCode);
        urlRepository.save(URL);
        return ShortCode;
    }

    public String getOriginalURl(String shorturl){
        URL url=urlRepository.findByShortUrl(shorturl);
        if(url==null){
            throw new RuntimeException("No ShortUrl Found");
        }
        return url.getOriginalUrl();
    }
}
