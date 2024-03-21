package com.example.repository;

import com.example.search.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;

@Repository
public interface ImageSearchRepository {
    Page<Tuple> searchImages(Pageable pageable);

    Page<Tuple> searchImagesFromBothTables(Pageable pageable, String keyword);

    Page<Tuple> filterImage(Pageable pageable, Filter filter);
}

