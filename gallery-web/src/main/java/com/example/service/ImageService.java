package com.example.service;

import com.example.dto.FilterDto;
import com.example.dto.ImageDisplayDto;
import com.example.dto.ImageDto;
import com.example.dto.TagDto;
import com.example.entity.ImageEntity;
import com.example.entity.TagEntity;
import com.example.repository.ImageRepository;
import com.example.repository.ImageSearchRepository;
import com.example.search.Search;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.util.Utils.convertBase64StringToByteArray;
import static com.example.util.Utils.createThumbnail;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final ImageSearchRepository imageSearchRepository;
    private final TagService tagService;

    public ImageEntity convertToEntity(ImageDto imageDto) throws IOException {
        ImageEntity imageEntity;

        if (imageDto.getId() != null) {
            imageEntity = imageRepository.findById(imageDto.getId())
                    .orElseThrow(() ->
                            new NoSuchElementException("Image with id " + imageDto.getId() + " does not exist"));
        } else {
            imageEntity = new ImageEntity();
            imageEntity.setUploadDate(LocalDate.now());
        }

        imageEntity.setImageData(convertBase64StringToByteArray(imageDto.getImageData()));
        imageEntity.setImageThumbnail(
                createThumbnail(convertBase64StringToByteArray(imageDto.getImageThumbnail()), 250));
        imageEntity.setName(imageDto.getName());
        imageEntity.setDescription(imageDto.getDescription());
        imageEntity.setTags(mapTagsToEntities(imageDto));

        return imageEntity;
    }

    private Set<TagEntity> mapTagsToEntities(ImageDto imageDto) {
        return imageDto.getTags().stream()
                .map(tagService::createOrUpdateTag)
                .collect(Collectors.toSet());
    }

    public Page<ImageDisplayDto> findAllImages(Pageable pageable) {
        Page<Tuple> page = imageSearchRepository.searchImages(pageable);

        List<ImageDisplayDto> imageDisplayDtoList = page.stream()
                .map(ImageDisplayDto::of)
                .collect(Collectors.toList());

        return new PageImpl<>(imageDisplayDtoList, pageable, page.getTotalElements());
    }

    public Page<ImageDisplayDto> findAllImagesByKeyword(Pageable pageable, String keyword) {
        Page<Tuple> page = imageSearchRepository.searchImagesFromBothTables(pageable, keyword);

        List<ImageDisplayDto> imageDisplayDtoList = page.stream()
                .map(ImageDisplayDto::of)
                .collect(Collectors.toList());

        return new PageImpl<>(imageDisplayDtoList, pageable, page.getTotalElements());
    }

    public Page<ImageDisplayDto> filterImage(FilterDto filterDto, Pageable pageable) {
        List<TagEntity> tags = new ArrayList<>();
        for (TagDto tag : filterDto.getTags()) {
            if (tagService.tagExists(tag.getTagName())) {
                tags.add(tagService.convertToEntity(tag));
            }
        }

        Search search = new Search(
                tags,
                filterDto.getDateFrom(),
                filterDto.getDateTo()
        );

        Page<Tuple> page = imageSearchRepository.filterImage(pageable, search);

        List<ImageDisplayDto> imageDisplayDtoList = page.stream()
                .map(ImageDisplayDto::of)
                .collect(Collectors.toList());

        return new PageImpl<>(imageDisplayDtoList, pageable, page.getTotalElements());
    }

    public ImageDto findImageById(Long id) {
        return ImageDto.of(imageRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Image with id " + id + " does not exist")));
    }

    public void createOrUpdateImage(ImageDto imageDto) throws IOException {
        ImageEntity imageEntity = convertToEntity(imageDto);
        imageRepository.save(imageEntity);
    }

    public void deleteImage(Long id) {
        imageRepository.deleteById(id);
    }
}