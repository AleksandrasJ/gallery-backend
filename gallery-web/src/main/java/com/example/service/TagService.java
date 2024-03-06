package com.example.service;

import com.example.database.TagEntity;
import com.example.dto.TagDto;
import com.example.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public TagEntity convertToEntity(TagDto tagDto) {
        TagEntity tagEntity;

        if (tagDto.getId() != null) {
            tagEntity = tagRepository.findById(tagDto.getId())
                    .orElseThrow(NoSuchElementException::new);
        } else {
            tagEntity = tagRepository.findByTagName(tagDto.getTagName())
                    .orElse(new TagEntity());
        }

        tagEntity.setTagName(tagDto.getTagName());

        return tagEntity;
    }

    public TagDto findTagById(Long id) {
        return TagDto.of(tagRepository.findById(id)
                .orElseThrow(NoSuchElementException::new));
    }

    public TagDto findTagByName(String tagName) {
        return TagDto.of(tagRepository.findByTagName(tagName)
                .orElseThrow(() -> new RuntimeException("Tag with name " + tagName + " not found!")));
    }

    public List<TagDto> findAllTags() {
        return tagRepository.findAll().stream()
                .map(TagDto::of)
                .collect(Collectors.toList());
    }

    public TagEntity createOrUpdateTag(TagDto tagDto) {
        TagEntity tagEntity = convertToEntity(tagDto);

        return tagRepository.save(tagEntity);
    }

    public void deleteTag(Long id) {
        tagRepository.findById(id);
    }
}
