package com.example.specification;

import com.example.database.ImageEntity;
import com.example.database.TagEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public interface ImageSpecification {

    static Specification<ImageEntity> hasKeywordInName(String keyword) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("name"), "%" + keyword + "%");
    }

    static Specification<ImageEntity> hasKeywordInDescription(String keyword) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("description"), "%" + keyword + "%");
    }

    //between
    static Specification<ImageEntity> hasDate(LocalDate fromDate, LocalDate toDate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("uploadDate"), fromDate, toDate);
    }

    static Specification<ImageEntity> hasTag(TagEntity tag) {
        return ((root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.isMember(tag, root.get("tags")));
    }
}
