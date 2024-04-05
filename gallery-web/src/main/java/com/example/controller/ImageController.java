package com.example.controller;

import com.example.dto.ImageDisplayDto;
import com.example.dto.ImageDto;
import com.example.search.Filter;
import com.example.service.ImageService;
import com.example.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.example.util.Utils.convertByteArrayToBase64String;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gallery")
public class ImageController {

    private final ImageService imageService;
    private final TagService tagService;

    @GetMapping("/image")
    public ResponseEntity<Page<ImageDisplayDto>> getAllImagesForDisplay(@RequestParam(name = "page", defaultValue = "0") int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 8);

        Page<ImageDisplayDto> page = imageService.findAllImages(pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(page);
    }

    @GetMapping("/image/photo/{id}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long id) {

        CacheControl cacheControl = CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic();
        return ResponseEntity
                .status(HttpStatus.OK)
                .cacheControl(cacheControl)
                .body(imageService.getPhotoById(id));
    }

    @GetMapping("/image/details/{id}")
    public ResponseEntity<ImageDto> getDetails(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(imageService.getPhotoDetails(id));
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<ImageDto> getImageForDisplay(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(imageService.findImageById(id));
    }

    @GetMapping("/image/search/{keyword}")
    public ResponseEntity<Page<ImageDisplayDto>> searchImagesByKeyword(@RequestParam(name = "page", defaultValue = "0") int pageNumber,
                                                                       @PathVariable String keyword) {
        Pageable pageable = PageRequest.of(pageNumber, 8);

        Page<ImageDisplayDto> page = imageService.findAllImagesByKeyword(pageable, keyword);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(page);
    }

    @PostMapping("/image/filter")
    public ResponseEntity<Page<ImageDisplayDto>> filterImages(@RequestParam(name = "page", defaultValue = "0") int pageNumber,
                                                              @RequestBody Filter filter) {
        Pageable pageable = PageRequest.of(pageNumber, 8);

        return ResponseEntity.status(HttpStatus.OK).body(imageService.filterImage(filter, pageable));
    }

    @GetMapping("/tag/{name}")
    public ResponseEntity<Long> getTagId(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(tagService.findTagByName(name).getId());
    }

    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestPart("information") ImageDto imageDto,
                                              @RequestPart("image") MultipartFile image) throws IOException {

        imageDto.setImageData(convertByteArrayToBase64String(image.getBytes()));
        imageDto.setImageThumbnail(convertByteArrayToBase64String(image.getBytes()));

        imageService.createOrUpdateImage(imageDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PutMapping("/image/{id}")
    public ResponseEntity<String> updateImage(@PathVariable Long id, @RequestBody ImageDto info) throws IOException {

        imageService.createOrUpdateImage(info);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);

    }

    @DeleteMapping("/image/{id}")
    public ResponseEntity<String> deleteImageById(@PathVariable Long id) {
        imageService.deleteImage(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
