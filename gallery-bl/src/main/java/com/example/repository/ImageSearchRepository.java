package com.example.repository;

import com.example.entity.ImageEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.util.List;

@Repository
public interface ImageSearchRepository {
    List<Tuple> searchImages(Pageable pageable);

    List<Tuple> searchImages(Specification<ImageEntity> spec);
}

