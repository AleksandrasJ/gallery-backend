package com.example.specification;

import com.example.entity.ImageEntity;
import com.example.entity.TagEntity;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
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

    static Specification<ImageEntity> hasDate(LocalDate fromDate, LocalDate toDate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("uploadDate"), fromDate, toDate);
    }

    static Specification<ImageEntity> hasTag(Long tagId) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.isMember(tagId, root.get("tags"));
    }

    static Specification<ImageEntity> hasKeywordInTag(String keyword) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<ImageEntity, TagEntity> tagEntityJoin = root.join("tags");
            return criteriaBuilder.like(tagEntityJoin.get("tagName"), "%" + keyword + "%");
        };
    }
}
