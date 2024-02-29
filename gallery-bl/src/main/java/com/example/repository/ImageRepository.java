package com.example.repository;

import com.example.database.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

    @Query(value = "SELECT i.id, i.image_data, i.image_name FROM images_table i", nativeQuery = true)
    public List<ImageEntity> findImagesForDisplay();

}
