package org.example;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class DocumentManager {

    private final Map<String, Document> storage = new HashMap<>();

    public Document save(Document document) {
        if (document.getId() == null || document.getId().isEmpty()) {
            document.setId(UUID.randomUUID().toString());
        }
        storage.put(document.getId(), document);
        return document;
    }

    public List<Document> search(SearchRequest request) {
        return storage.values().stream().filter(document -> match(document,request)).collect(Collectors.toList());
    }

    public Optional<Document> findById(String id) {

        return Optional.ofNullable(storage.get(id));
    }

    private boolean match(Document document, SearchRequest request) {
        if (request.getTitlePrefixes() != null && request.getTitlePrefixes().stream()
                .noneMatch(prefix -> document.getTitle().startsWith(prefix))) {
            return false;
        }
        if (request.getContainsContents() != null && request.getContainsContents().stream()
                .noneMatch(content -> document.getContent().contains(content))) {
            return false;
        }
        if (request.getAuthorIds() != null && !request.getAuthorIds().contains(document.getAuthor().getId())) {
            return false;
        }
        if (request.getCreatedFrom() != null && document.getCreated().isBefore(request.getCreatedFrom())) {
            return false;
        }
        return request.getCreatedTo() == null || !document.getCreated().isAfter(request.getCreatedTo());
    }
    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}