package com.nayalens.document.repository;

import com.nayalens.document.model.LegalDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryDocumentRepository implements DocumentRepository {

    private final Map<String, StoredDocumentEntry> storage = new ConcurrentHashMap<>();
    private final Duration sessionTtl;

    public InMemoryDocumentRepository(@Value("${nyayalens.document.session-ttl-minutes:60}") int sessionTtlMinutes) {
        this.sessionTtl = Duration.ofMinutes(sessionTtlMinutes);
    }

    private record StoredDocumentEntry(
        LegalDocument document,
        Instant lastAccessedAt
    ) {}

    @Override
    public LegalDocument save(LegalDocument document) {
        Objects.requireNonNull(document, "Document cannot be null");
        storage.put(document.id(), new StoredDocumentEntry(document, Instant.now()));
        return document;
    }

    @Override
    public Optional<LegalDocument> findById(String id) {
        if (id == null) return Optional.empty();
        StoredDocumentEntry entry = storage.get(id);
        if (entry == null) return Optional.empty();

        // Update last accessed time
        storage.put(id, new StoredDocumentEntry(entry.document(), Instant.now()));
        return Optional.of(entry.document());
    }

    @Override
    public List<LegalDocument> findAll() {
        return storage.values().stream()
                .map(StoredDocumentEntry::document)
                .toList();
    }

    @Override
    public boolean deleteById(String id) {
        if (id == null) return false;
        return storage.remove(id) != null;
    }

    @Override
    public void deleteAll() {
        storage.clear();
    }

    @Scheduled(fixedRate = 600000) // Every 10 minutes
    public void purgeExpiredDocuments() {
        Instant threshold = Instant.now().minus(sessionTtl);
        storage.entrySet().removeIf(e -> e.getValue().lastAccessedAt().isBefore(threshold));
    }
}
