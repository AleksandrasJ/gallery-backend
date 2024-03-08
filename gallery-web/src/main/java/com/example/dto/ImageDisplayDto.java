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

    public static ImageDisplayDto of(Object[] image) {
        return ImageDisplayDto.builder()
                .id((Long) image[0])
                .imageThumbnail(convertByteArrayToBase64String((byte[]) image[1]))
                .build();
    }

    public static ImageDisplayDto of(ImageEntity entity) {
        return ImageDisplayDto.builder()
                .id(entity.getId())
                .imageThumbnail(convertByteArrayToBase64String(entity.getImageThumbnail()))
                .build();
    }

    public static ImageDisplayDto of(Tuple entity) {
        return ImageDisplayDto.builder()
                .id((Long) entity.get(0))
                .imageThumbnail(convertByteArrayToBase64String((byte[]) entity.get(1)))
                .build();
    }
}
