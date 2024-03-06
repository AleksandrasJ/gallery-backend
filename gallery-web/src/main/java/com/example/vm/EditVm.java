package com.example.vm;

import com.example.dto.ImageDto;
import com.example.service.ImageService;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

import java.io.IOException;

import static com.example.util.Utils.convertSetToString;
import static com.example.util.Utils.convertStringToSet;

@Getter
@Setter
@VariableResolver(DelegatingVariableResolver.class)
public class EditVm {

    @WireVariable
    private ImageService imageService;

    private ImageDto imageDto;
    private String tags;

    @Init
    public void init(@QueryParam("id") Long id) throws IOException {
        imageDto = imageService.findImageById(id);
        tags = convertSetToString(imageDto.getTags());
    }

    @Command
    public void doImageUpdate() {
        imageDto.setTags(convertStringToSet(tags));
        if (imageDto.getImageData() != null && !imageDto.getName().isEmpty()) {
            imageService.createOrUpdateImage(imageDto);
            Executions.sendRedirect("index.zul");
        }

        Clients.showNotification("Name should not be empty.",
                "warning", null, "middle_center", 0);
    }
}
