package com.example.dto;

import com.example.entity.ImageEntity;
import com.example.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Tuple;

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
                .imageThumbnail(Utils.convertByteArrayToBase64String(entity.getImageThumbnail()))
                .build();
    }

    public static ImageDisplayDto of(Tuple entity) {
        Long id = entity.get("id", Long.class);
        byte[] imageThumbnail = entity.get("imageThumbnail", byte[].class);

        return ImageDisplayDto.builder()
                .id(id)
                .imageThumbnail(Utils.convertByteArrayToBase64String(imageThumbnail))
                .build();
    }
}
