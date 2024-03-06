package com.example.converter;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.image.AImage;
import org.zkoss.zul.Image;

import java.io.IOException;

import static com.example.util.Utils.convertBase64StringToByteArray;

public class ImageToZkImageConverter implements Converter<AImage, String, Image> {
    @Override
    public AImage coerceToUi(String beanProp, Image component, BindContext ctx) {
        try {
            if (beanProp != null) {
                AImage image = new AImage("", convertBase64StringToByteArray(beanProp));
                component.setContent(image);
                return image;
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String coerceToBean(AImage compAttr, Image component, BindContext ctx) {
        return null;
    }
}
