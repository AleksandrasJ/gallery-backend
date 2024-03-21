package com.example.dto;

import com.example.entity.ImageEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Tuple;

import static com.example.util.Utils.convertByteArrayToBase64String;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ImageDisplayDto {

    private Long id;
    private String imageThumbnail;

    public static ImageDisplayDto of(ImageEntity entity) {
        return ImageDisplayDto.builder()
                .id(entity.getId())
                .imageThumbnail(convertByteArrayToBase64String(entity.getImageThumbnail()))
                .build();
    }

    public static ImageDisplayDto of(Tuple entity) {
        Long id = entity.get("id", Long.class);
        byte[] imageThumbnail = entity.get("imageThumbnail", byte[].class);

        return ImageDisplayDto.builder()
                .id(id)
                .imageThumbnail(convertByteArrayToBase64String(imageThumbnail))
                .build();
    }
}
