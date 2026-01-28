package com.rihan.linkshortenerservice.api;

import com.rihan.linkshortenerservice.core.RandomCode;
import com.rihan.linkshortenerservice.core.UrlHash;
import com.rihan.linkshortenerservice.url.UrlMapping;
import com.rihan.linkshortenerservice.url.UrlMappingRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import jakarta.validation.Valid;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.time.Duration;

import java.net.URI;

@RestController
public class LinkController {

    private final UrlMappingRepository repo;
    private final StringRedisTemplate redis;

    public LinkController(UrlMappingRepository repo,StringRedisTemplate redis) {
        this.repo = repo;
        this.redis = redis;
    }

    public record CreateLinkRequest(
            @NotBlank(message = "Please paste a URL.")
            @URL(message = "Invalid URL. Example: https://google.com")
            String longUrl
    ) {}
    public record CreateLinkResponse(String code, String shortUrl, String longUrl) {}


//    Sliding window means “allow at most N requests in the last X seconds”, not “per calendar minute”. This avoids the burst-at-boundary problem of fixed windows.

//
//    Algorithm (per user/IP key)
//    For each request:
//    ZADD key now member (store timestamp)
//    ZREMRANGEBYSCORE key 0 (now - windowMs) (remove old entries)
//    ZCARD key (count requests in window)
//    If count > limit → reject with 429
//    EXPIRE key windowSeconds so Redis cleans it up


    @PostMapping("/api/v1/links")
    public ResponseEntity<CreateLinkResponse> create(@Valid @RequestBody CreateLinkRequest req) {
        if (req == null || req.longUrl() == null || req.longUrl().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        String longUrl = req.longUrl().trim();
        String urlHash = UrlHash.sha256Hex(longUrl);

        // 1) Dedupe: if already exists, return same short link
        UrlMapping existing = repo.findByUrlHash(urlHash).orElse(null);
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        if (existing != null) {
            String shortUrl = baseUrl + "/" + existing.getCode();
            return ResponseEntity.ok(new CreateLinkResponse(existing.getCode(), shortUrl, existing.getLongUrl()));
        }

        // 2) Create new row; retry if random code collides
        UrlMapping mapping = repo.save(new UrlMapping(longUrl, urlHash));

        for (int attempt = 0; attempt < 10; attempt++) {
            String code = RandomCode.generate(8);
            mapping.setCode(code);
            try {
                repo.save(mapping);
                String shortUrl = baseUrl + "/" + code;
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new CreateLinkResponse(code, shortUrl, mapping.getLongUrl()));
            } catch (DataIntegrityViolationException ex) {
                // code collision -> retry
            }
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String key = "code:" + code;
//        // 1) Cache hit
        String longUrl = redis.opsForValue().get(key);
        if (longUrl != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(longUrl));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
//
//        // 2) Cache miss -> DB
        UrlMapping m = repo.findByCode(code).orElse(null);
        if (m == null) return ResponseEntity.notFound().build();

//        // 3) Populate cache (TTL optional)
        redis.opsForValue().set(key, m.getLongUrl(), Duration.ofHours(24));

//        UrlMapping m = repo.findByCode(code).orElse(null);
//        if (m == null) return ResponseEntity.notFound().build();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(m.getLongUrl()));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
