package com.example.implementation;

import com.example.entity.ImageEntity;
import com.example.repository.ImageSearchRepository;
import com.example.search.Filter;
import com.example.specification.ImageSpecification;
import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    public Page<Tuple> searchImages(Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
        Root<ImageEntity> imageRoot = query.from(ImageEntity.class);

        Path<Object> id = imageRoot.get("id");
        Path<Object> imageThumbnail = imageRoot.get("imageThumbnail");

        query.multiselect(
                id.alias("id"),
                imageThumbnail.alias("imageThumbnail")
        );

        return getTuples(pageable, query);
    }

    public Page<Tuple> searchImagesFromBothTables(Pageable pageable, String keyword) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
        Root<ImageEntity> imageRoot = query.from(ImageEntity.class);

        Path<Object> id = imageRoot.get("id");
        Path<Object> imageThumbnail = imageRoot.get("imageThumbnail");

        query.multiselect(
                id.alias("id"),
                imageThumbnail.alias("imageThumbnail")
        );

        Predicate finalPredicate = getSearchPredicate(keyword, imageRoot, query, criteriaBuilder);

        query.where(finalPredicate);

        return getTuples(pageable, query);
    }

    private static Predicate getSearchPredicate(String keyword, Root<ImageEntity> imageRoot, CriteriaQuery<Tuple> query, CriteriaBuilder criteriaBuilder) {
        Predicate keywordInDescription = ImageSpecification.hasKeywordInDescription(keyword)
                .toPredicate(imageRoot, query, criteriaBuilder);
        Predicate keywordInName = ImageSpecification.hasKeywordInName(keyword)
                .toPredicate(imageRoot, query, criteriaBuilder);

        Subquery<Long> tagSubquery = query.subquery(Long.class);
        Root<ImageEntity> subImageRoot = tagSubquery.from(ImageEntity.class);

        Predicate keywordInTagName = ImageSpecification.hasKeywordInTag(keyword)
                .toPredicate(subImageRoot, query, criteriaBuilder);

        tagSubquery.select(subImageRoot.get("id")).where(keywordInTagName);
        Predicate tagName = imageRoot.get("id").in(tagSubquery);

        return criteriaBuilder.or(keywordInDescription, keywordInName, tagName);
    }

    @Override
    public Page<Tuple> filterImage(Pageable pageable, Filter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
        Root<ImageEntity> imageEntityRoot = query.from(ImageEntity.class);

        Path<ImageEntity> id = imageEntityRoot.get("id");
        Path<ImageEntity> imageThumbnail = imageEntityRoot.get("imageThumbnail");

        query.multiselect(
                id.alias("id"),
                imageThumbnail.alias("imageThumbnail")
        );

        List<Predicate> predicateList = getFilerPredicates(filter, imageEntityRoot, query, criteriaBuilder);

        if (!predicateList.isEmpty()) {
            query.where(predicateList.toArray(new Predicate[]{}));
        }

        return getTuples(pageable, query);
    }

    private static List<Predicate> getFilerPredicates(Filter filter, Root<ImageEntity> imageEntityRoot, CriteriaQuery<Tuple> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicateList = new ArrayList<>();

        if (!filter.getTagsIds().isEmpty()) {
            for (Long tag : filter.getTagsIds()) {
                predicateList.add(ImageSpecification.hasTag(tag).toPredicate(imageEntityRoot, query, criteriaBuilder));
            }
        }

        if (filter.getDateFrom() != null && filter.getDateTo() != null) {
            Predicate isBetweenDate = filter.isBetweenDates().toPredicate(imageEntityRoot, query, criteriaBuilder);
            predicateList.add(isBetweenDate);
        }
        return predicateList;
    }

    @NotNull
    private Page<Tuple> getTuples(Pageable pageable, CriteriaQuery<Tuple> query) {
        TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);

        List<Tuple> resultList = typedQuery.getResultList();

        int first = Math.min(new Long(pageable.getOffset()).intValue(), resultList.size());
        int last = Math.min(first + pageable.getPageSize(), resultList.size());
        long totalCount = resultList.size();

        return new PageImpl<>(resultList.subList(first, last), pageable, totalCount);
    }
}
