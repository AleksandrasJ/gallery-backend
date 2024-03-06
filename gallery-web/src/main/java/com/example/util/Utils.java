package com.example.util;

import com.example.dto.TagDto;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    public static byte[] createThumbnail(byte[] imageData, int size) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
        BufferedImage bufferedImage = ImageIO.read(bis);
        BufferedImage resizedImage = Scalr.resize(bufferedImage, size);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "png", bos);
        return bos.toByteArray();
    }

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
