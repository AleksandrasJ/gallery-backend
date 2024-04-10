package com.example.utils;

import java.util.Base64;

public class Utils {

    public static String convertByteArrayToBase64String(byte[] imageData) {
        return Base64.getEncoder().encodeToString(imageData);
    }

    public static byte[] convertBase64StringToByteArray(String imageData) {
        return Base64.getDecoder().decode(imageData);
    }
}
