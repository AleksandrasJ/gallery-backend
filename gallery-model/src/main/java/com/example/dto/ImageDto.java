package com.example.dto;

import com.example.entity.ImageEntity;
import com.example.entity.TagEntity;
import com.example.utils.Utils;
import lombok.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

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
                .imageData(Utils.convertByteArrayToBase64String(entity.getImageData()))
                .imageThumbnail(Utils.convertByteArrayToBase64String(entity.getImageThumbnail()))
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

    public static ImageDto of(Object[] result) {
        Long id = (Long) result[0];
        String name = (String) result[1];
        String description = (String) result[2];
        LocalDate uploadDate = (LocalDate) result[3];

        return ImageDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .uploadDate(uploadDate)
                .tags(mapTagsToDtos(result))
                .build();
    }

    private static Set<TagDto> mapTagsToDtos(Object[] result) {
        TagEntity[] tagEntities = (TagEntity[]) result[4];
        return Arrays.stream(tagEntities)
                .map(TagDto::of)
                .collect(Collectors.toSet());
    }
}
