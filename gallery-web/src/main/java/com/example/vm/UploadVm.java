package com.example.vm;

import com.example.dto.ImageDto;
import com.example.service.ImageService;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

import java.io.IOException;

import static com.example.util.Utils.convertStringToSet;
import static com.example.util.Utils.createThumbnail;

@Getter
@Setter
@VariableResolver(DelegatingVariableResolver.class)
public class UploadVm {

    @WireVariable
    private ImageService imageService;

    private ImageDto imageDto;
    private String imageName;
    private byte[] thumbnail;
    private String tags;

    @Init
    public void init() {
        imageDto = new ImageDto(
                null,
                null,
                null,
                "",
                null,
                null,
                null);
        tags = "";
    }

    @Command
    public void doImageSave() {
        imageDto.setTags(convertStringToSet(tags));
        if (imageDto.getImageData() != null && !imageDto.getName().isEmpty()) {
            imageService.createOrUpdateImage(imageDto);
            Executions.sendRedirect("index.zul");
        }

        Clients.showNotification("You need to select image and insert name before uploading it.",
                "warning", null, "middle_center", 0);
    }

    @Command
    @NotifyChange({"imageName", "imageDto", "thumbnail"})
    public void doFileUpload(@ContextParam(ContextType.BIND_CONTEXT) BindContext photo) throws IOException {
        UploadEvent uploadEvent = (UploadEvent) photo.getTriggerEvent();
        Media media = uploadEvent.getMedia();
        byte[] imageData = media.getByteData();

        imageName = media.getName();
        thumbnail = createThumbnail(imageData, 400);
        imageDto.setImageData(imageData);
    }
}
