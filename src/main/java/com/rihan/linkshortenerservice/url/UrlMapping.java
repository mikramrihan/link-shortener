package com.rihan.linkshortenerservice.url;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "url_mapping")
public class UrlMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, length = 8)
    private String code;

    @Column(name = "url_hash", unique = true, nullable = false, length = 64)
    private String urlHash; // hex sha-256 = 64 chars

    @Column(name = "long_url", nullable = false, length = 2048)
    private String longUrl;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UrlMapping() {}

    public UrlMapping(String longUrl, String urlHash) {
        this.longUrl = longUrl;
        this.urlHash = urlHash;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getUrlHash() { return urlHash; }
    public String getLongUrl() { return longUrl; }
    public Instant getCreatedAt() { return createdAt; }

    public void setCode(String code) { this.code = code; }
}
