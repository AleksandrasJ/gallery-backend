package com.example.util;

import com.example.dto.TagDto;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    public static String convertSetToString(Set<TagDto> tags) {
        if (tags.isEmpty()) {
            return "";
        }

        return tags.stream()
                .map(TagDto::getTagName)
                .collect(Collectors.joining(", "));
    }

    public static Set<TagDto> convertStringToSet(String tags) {
        if (tags.isEmpty()) {
            return new HashSet<>();
        }

        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .map(tag -> new TagDto(null, tag))
                .collect(Collectors.toSet());
    }

    public static String convertByteArrayToBase64String(byte[] imageData) {
        return Base64.getEncoder().encodeToString(imageData);
    }

    public static byte[] convertBase64StringToByteArray(String imageData) {
        return Base64.getDecoder().decode(imageData);
    }

    public static LocalDate convertDateToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
