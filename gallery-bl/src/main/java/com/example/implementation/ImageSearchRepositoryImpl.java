package com.example.implementation;

import com.example.entity.ImageEntity;
import com.example.entity.TagEntity;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ImageSearchRepositoryImpl implements ImageSearchRepository {

    private final EntityManager entityManager;

    @Override
    public byte[] getPhoto(Long id) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<byte[]> query = criteriaBuilder.createQuery(byte[].class);
        Root<ImageEntity> imageRoot = query.from(ImageEntity.class);

        Path<byte[]> photo = imageRoot.get("imageData");

        query.select(photo.alias("photo")).where(criteriaBuilder.equal(imageRoot.get("id"), id));

        return entityManager.createQuery(query).getSingleResult();
    }

    @Override
    public Object[] getPhotoDetails(Long requestId) {
        Tuple imageTuple = getImageDetails(requestId);
        List<TagEntity> tags = getTagsForImage(requestId);


        Object[] result = new Object[5];
        result[0] = imageTuple.get("id", Long.class);
        result[1] = imageTuple.get("name", String.class);
        result[2] = imageTuple.get("description", String.class);
        result[3] = imageTuple.get("uploadDate", LocalDate.class);
        result[4] = tags.toArray(new TagEntity[0]);

        return result;
    }

    private Tuple getImageDetails(Long requestId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> imageQuery = criteriaBuilder.createTupleQuery();
        Root<ImageEntity> imageRoot = imageQuery.from(ImageEntity.class);

        Path<Long> idPath = imageRoot.get("id");
        Path<String> namePath = imageRoot.get("name");
        Path<String> descriptionPath = imageRoot.get("description");
        Path<LocalDate> uploadDatePath = imageRoot.get("uploadDate");

        imageQuery.multiselect(
                idPath.alias("id"),
                namePath.alias("name"),
                descriptionPath.alias("description"),
                uploadDatePath.alias("uploadDate")
        ).where(criteriaBuilder.equal(imageRoot.get("id"), requestId));

        return entityManager.createQuery(imageQuery).getSingleResult();
    }

    private List<TagEntity> getTagsForImage(Long requestId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TagEntity> tagQuery = criteriaBuilder.createQuery(TagEntity.class);
        Root<TagEntity> tagRoot = tagQuery.from(TagEntity.class);
        Join<TagEntity, ImageEntity> imageJoin = tagRoot.join("images", JoinType.INNER);

        tagQuery.select(tagRoot)
                .where(criteriaBuilder.equal(imageJoin.get("id"), requestId));

        return entityManager.createQuery(tagQuery).getResultList();
    }

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
