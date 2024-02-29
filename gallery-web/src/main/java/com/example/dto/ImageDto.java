package com.example.dto;

import com.example.database.ImageEntity;
import lombok.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.util.Utils.createThumbnail;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ImageDto {

    private Long id;
    private byte[] imageData;
    private byte[] thumbnail;
    private String name;
    private String description;
    private LocalDate uploadDate;
    private Set<TagDto> tags;

    public static ImageDto of(ImageEntity entity) {
        try {
            return ImageDto.builder()
                    .id(entity.getId())
                    .imageData(entity.getImageData())
                    .thumbnail(createThumbnail(entity.getImageData(), 180))
                    .name(entity.getName())
                    .description(entity.getDescription())
                    .uploadDate(entity.getUploadDate())
                    .tags(mapTagsToDtos(entity))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Set<TagDto> mapTagsToDtos(ImageEntity entity) {
        return entity.getTags().stream()
                .map(TagDto::of)
                .collect(Collectors.toSet());
    }
}
