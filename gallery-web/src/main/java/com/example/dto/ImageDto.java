package com.example.dto;

import com.example.entity.ImageEntity;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.util.Utils.convertByteArrayToBase64String;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {

    private Long id;
    private String imageData;
    private String imageThumbnail;
    private String name;
    private String description;
    private LocalDate uploadDate;
    private Set<TagDto> tags;

    public static ImageDto of(ImageEntity entity) {
        return ImageDto.builder()
                .id(entity.getId())
                .imageData(convertByteArrayToBase64String(entity.getImageData()))
                .imageThumbnail(convertByteArrayToBase64String(entity.getImageThumbnail()))
                .name(entity.getName())
                .description(entity.getDescription())
                .uploadDate(entity.getUploadDate())
                .tags(mapTagsToDtos(entity))
                .build();
    }

    private static Set<TagDto> mapTagsToDtos(ImageEntity entity) {
        return entity.getTags().stream()
                .map(TagDto::of)
                .collect(Collectors.toSet());
    }
}
