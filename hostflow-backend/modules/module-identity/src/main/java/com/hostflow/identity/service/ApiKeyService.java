package com.hostflow.identity.service;

import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.identity.entity.ApiKey;
import com.hostflow.identity.repository.ApiKeyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class ApiKeyService {

    private static final String KEY_PREFIX = "rvk_";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ApiKeyRepository repository;

    public ApiKeyService(ApiKeyRepository repository) {
        this.repository = repository;
    }

    public record GeneratedKey(ApiKey entity, String rawKey) {
    }

    @Transactional
    public GeneratedKey create(String name) {
        byte[] randomBytes = new byte[32];
        RANDOM.nextBytes(randomBytes);
        String rawKey = KEY_PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        String hash = hash(rawKey);
        String displayPrefix = rawKey.substring(0, Math.min(rawKey.length(), 12)) + "...";

        ApiKey entity = repository.save(new ApiKey(name, hash, displayPrefix));
        return new GeneratedKey(entity, rawKey);
    }

    @Transactional(readOnly = true)
    public List<ApiKey> list() {
        return repository.findAll();
    }

    @Transactional
    public void revoke(UUID id) {
        ApiKey key = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ApiKey", id));
        key.revoke();
    }

    public static String hash(String rawKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawKey.getBytes());
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
