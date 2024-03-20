package com.example.repository;

import com.example.search.Search;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.util.List;

@Repository
public interface ImageSearchRepository {
    List<Tuple> searchImages(Pageable pageable);

    List<Tuple> searchImagesFromBothTables(String keyword);

    List<Tuple> filterImage(Search search, Pageable pageable);
}

