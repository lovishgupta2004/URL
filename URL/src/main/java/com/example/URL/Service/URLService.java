package com.example.URL.Service;

import com.example.URL.Repository.URLRepository;
import com.example.URL.Entity.URL;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class URLService {
    private final URLRepository urlRepository;

    public URLService(URLRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

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

    public String ShortenURLCustom(String originalUrl, String CustomUrl){
        if(urlRepository.existsByShortUrl(CustomUrl)){
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
        if(url==null){
            throw new RuntimeException("No ShortUrl Found");
        }
        return url.getOriginalUrl();
    }
}
