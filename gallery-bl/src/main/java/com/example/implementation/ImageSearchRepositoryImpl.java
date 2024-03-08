package com.example.implementation;

import com.example.entity.ImageEntity;
import com.example.repository.ImageSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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

    @Override
    public List<Tuple> searchImages(Specification<ImageEntity> spec) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
        Root<ImageEntity> imageRoot = query.from(ImageEntity.class);

        query.multiselect(
                imageRoot.get("id"),
                imageRoot.get("imageThumbnail")
        );

        if (spec != null) {
            query.where(spec.toPredicate(imageRoot, query, criteriaBuilder));
        }

        return entityManager.createQuery(query).getResultList();
    }
}
