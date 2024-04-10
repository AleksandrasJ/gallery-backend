package com.example.repository;

import com.example.filter.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;

@Repository
public interface ImageSearchRepository {

    byte[] getPhoto(Long id);

    Object[] getPhotoDetails(Long id);

    Page<Tuple> searchImages(Pageable pageable);

    Page<Tuple> searchImagesFromBothTables(Pageable pageable, String keyword);

    Page<Tuple> filterImage(Pageable pageable, Filter filter);
}

