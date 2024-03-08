package com.example.service;

import com.example.dto.ImageDisplayDto;
import com.example.dto.ImageDto;
import com.example.dto.TagDto;
import com.example.entity.ImageEntity;
import com.example.entity.TagEntity;
import com.example.repository.ImageRepository;
import com.example.repository.ImageSearchRepository;
import com.example.specification.ImageSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.util.Utils.convertBase64StringToByteArray;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final ImageSearchRepository imageSearchRepository;
    private final TagService tagService;

    public ImageEntity convertToEntity(ImageDto imageDto) {
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
        imageEntity.setImageThumbnail(convertBase64StringToByteArray(imageDto.getImageThumbnail()));
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

    public List<ImageDisplayDto> findAllImages(int pageNumber, int pageSize) {
        return imageSearchRepository.searchImages(PageRequest.of(pageNumber, 8)).stream()
                .map(ImageDisplayDto::of)
                .collect(Collectors.toList());
    }

    public List<ImageDisplayDto> findAllImagesByKeyword(String keyword) {
        Specification<ImageEntity> specification = Specification
                .where(ImageSpecification.hasKeywordInName(keyword))
                .or(ImageSpecification.hasKeywordInDescription(keyword));

        return imageSearchRepository.searchImages(specification).stream()
                .map(ImageDisplayDto::of)
                .collect(Collectors.toList());
    }

    public List<ImageDisplayDto> findAllImagesByTag(TagDto tagDto) {
        return imageSearchRepository.searchImages(ImageSpecification.hasTag(tagService.convertToEntity(tagDto))).stream()
                .map(ImageDisplayDto::of)
                .collect(Collectors.toList());
    }

    public List<ImageDisplayDto> findAllImagesByDate(LocalDate fromDate, LocalDate toDate) {
        return imageSearchRepository.searchImages(ImageSpecification.hasDate(fromDate, toDate)).stream()
                .map(ImageDisplayDto::of)
                .collect(Collectors.toList());
    }

    public List<ImageDisplayDto> findAllImagesByDateAndTag(TagDto tagDto, LocalDate fromDate, LocalDate toDate) {
        Specification<ImageEntity> specification = Specification
                .where(ImageSpecification.hasTag(tagService.convertToEntity(tagDto)))
                .and(ImageSpecification.hasDate(fromDate, toDate));

        return imageSearchRepository.searchImages(specification).stream()
                .map(ImageDisplayDto::of)
                .collect(Collectors.toList());
    }

    public ImageDto findImageById(Long id) {
        return ImageDto.of(imageRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Image with id " + id + " does not exist")));
    }

    public void createOrUpdateImage(ImageDto imageDto) {
        ImageEntity imageEntity = convertToEntity(imageDto);
        imageRepository.save(imageEntity);
    }

    public void deleteImage(Long id) {
        imageRepository.deleteById(id);
    }
}