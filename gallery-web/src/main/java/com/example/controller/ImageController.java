package com.example.controller;

import com.example.dto.ImageDisplayDto;
import com.example.dto.ImageDto;
import com.example.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.example.util.Utils.convertByteArrayToBase64String;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gallery")
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/image")
    public ResponseEntity<Page<ImageDisplayDto>> getAllImagesForDisplay(@RequestParam(name = "page", defaultValue = "0") int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 8);

        Page<ImageDisplayDto> page = imageService.findAllImages(pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(page);
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<ImageDto> getImageForDisplay(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(imageService.findImageById(id));
    }

    // TODO: Search

    // TODO: Filter

    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestPart("information") ImageDto info,
                                              @RequestPart("image") MultipartFile image) throws IOException {
        ImageDto imageDto = new ImageDto();

        imageDto.setImageData(convertByteArrayToBase64String(image.getBytes()));
        imageDto.setImageThumbnail(convertByteArrayToBase64String(image.getBytes()));
        imageDto.setName(info.getName());
        imageDto.setDescription(info.getDescription());
        imageDto.setTags(info.getTags());

        imageService.createOrUpdateImage(imageDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PutMapping("/image/{id}")
    public ResponseEntity<String> updateImage(@PathVariable Long id, @RequestBody ImageDto info) throws IOException {
        ImageDto imageDto = imageService.findImageById(id);

        imageDto.setName(info.getName());
        imageDto.setDescription(info.getDescription());
        imageDto.setTags(info.getTags());

        imageService.createOrUpdateImage(imageDto);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);

    }

    @DeleteMapping("/image/{id}")
    public ResponseEntity<String> deleteImageById(@PathVariable Long id) {
        imageService.deleteImage(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
