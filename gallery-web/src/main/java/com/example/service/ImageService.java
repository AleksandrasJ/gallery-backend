package com.example.service;

import com.example.database.ImageEntity;
import com.example.database.TagEntity;
import com.example.dto.ImageDto;
import com.example.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final TagService tagService;

    public ImageEntity convertToEntity(ImageDto imageDto) {
        ImageEntity imageEntity;

        if (imageDto.getId() != null) {
            imageEntity = imageRepository.findById(imageDto.getId())
                    .orElseThrow(NoSuchElementException::new);
        } else {
            imageEntity = new ImageEntity();
        }

        imageEntity.setImageData(imageDto.getImageData());
        imageEntity.setName(imageDto.getName());
        imageEntity.setDescription(imageDto.getDescription());
        imageEntity.setUploadDate(LocalDate.now());
        imageEntity.setTags(mapTagsToEntities(imageDto));

        return imageEntity;
    }

    private Set<TagEntity> mapTagsToEntities(ImageDto imageDto) {
        return imageDto.getTags().stream()
                .map(tagService::convertToEntity)
                .collect(Collectors.toSet());
    }

    public ImageDto findImageById(Long id) {
        return ImageDto.of(imageRepository.findById(id)
                .orElseThrow(NoSuchElementException::new));
    }

    public void createOrUpdateImage(ImageDto imageDto) {
        ImageEntity imageEntity = convertToEntity(imageDto);
        imageRepository.save(imageEntity);
    }

    public void deleteImage(Long id) {
        imageRepository.deleteById(id);
    }


    public List<ImageDto> findAllImages() {
        return imageRepository.findAll().stream()
                .map(ImageDto::of)
                .collect(Collectors.toList());
    }
}