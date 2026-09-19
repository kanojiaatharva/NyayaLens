package com.nayalens.document.repository;

import com.nayalens.document.model.LegalDocument;
import java.util.List;
import java.util.Optional;

public interface DocumentRepository {
    LegalDocument save(LegalDocument document);
    Optional<LegalDocument> findById(String id);
    List<LegalDocument> findAll();
    boolean deleteById(String id);
    void deleteAll();
}
