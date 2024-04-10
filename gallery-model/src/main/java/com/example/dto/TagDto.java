package com.example.dto;

import com.example.entity.TagEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class TagDto {

    private final Long id;
    private final String tagName;

    public static TagDto of(TagEntity entity) {
        return TagDto.builder()
                .id(entity.getId())
                .tagName(entity.getTagName())
                .build();
    }
}
