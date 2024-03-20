package com.example.implementation;

import com.example.entity.ImageEntity;
import com.example.entity.TagEntity;
import com.example.repository.ImageSearchRepository;
import com.example.search.Search;
import com.example.specification.ImageSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ImageSearchRepositoryImpl implements ImageSearchRepository {

    private final EntityManager entityManager;

    @Override
    public List<Tuple> searchImages(Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
        Root<ImageEntity> imageRoot = query.from(ImageEntity.class);

        query.multiselect(
                imageRoot.get("id"),
                imageRoot.get("imageThumbnail")
        );

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(pageable.getPageNumber());
        typedQuery.setMaxResults(pageable.getPageSize());

        return typedQuery.getResultList();
    }

    public List<Tuple> searchImagesFromBothTables(String keyword) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
        Root<ImageEntity> imageRoot = query.from(ImageEntity.class);

        Path<Object> id = imageRoot.get("id");
        Path<Object> imageThumbnail = imageRoot.get("imageThumbnail");
        query.multiselect(
                id,
                imageThumbnail
        );

        Predicate keywordInDescription = ImageSpecification.hasKeywordInDescription(keyword).toPredicate(imageRoot, query, criteriaBuilder);
        Predicate keywordInName = ImageSpecification.hasKeywordInName(keyword).toPredicate(imageRoot, query, criteriaBuilder);

        Subquery<Long> tagSubquery = query.subquery(Long.class);
        Root<ImageEntity> subImageRoot = tagSubquery.from(ImageEntity.class);

        Predicate keywordInTagName = ImageSpecification.hasKeywordInTag(keyword).toPredicate(subImageRoot, query, criteriaBuilder);

        tagSubquery.select(subImageRoot.get("id")).where(keywordInTagName);
        Predicate tagName = criteriaBuilder.in(imageRoot.get("id")).value(tagSubquery);

        Predicate finalPredicate = criteriaBuilder.or(keywordInDescription, keywordInName, tagName);

        query.where(finalPredicate);

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<Tuple> filterImage(Search search, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
        Root<ImageEntity> imageEntityRoot = query.from(ImageEntity.class);

        Path<ImageEntity> id = imageEntityRoot.get("id");
        Path<ImageEntity> imageThumbnail = imageEntityRoot.get("imageThumbnail");

        query.multiselect(id, imageThumbnail);

        List<Predicate> predicateList = new ArrayList<>();
        if (!search.getTags().isEmpty()) {
            for (TagEntity tag : search.getTags()) {
                predicateList.add(ImageSpecification.hasTag(tag).toPredicate(imageEntityRoot, query, criteriaBuilder));
            }
        }

        if (search.getDateFrom() != null && search.getDateTo() != null) {
            Predicate isBetweenDate = search.isBetweenDates().toPredicate(imageEntityRoot, query, criteriaBuilder);
            predicateList.add(isBetweenDate);
        }

        if (!predicateList.isEmpty()) {
            query.where(predicateList.toArray(new Predicate[]{}));
        }

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(pageable.getPageNumber());
        typedQuery.setMaxResults(pageable.getPageSize());

        return typedQuery.getResultList();
    }
}
