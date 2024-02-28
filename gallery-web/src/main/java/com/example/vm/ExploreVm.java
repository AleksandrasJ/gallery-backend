package com.example.vm;

import com.example.dto.ImageDto;
import com.example.dto.TagDto;
import com.example.service.ImageService;
import lombok.Getter;
import lombok.Setter;
import org.imgscalr.Scalr;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.util.Utils.createThumbnail;

@Getter
@VariableResolver(DelegatingVariableResolver.class)
public class ExploreVm {

    @WireVariable
    ImageService imageService;

    private List<ImageDto> imageDtos;

    @Init
    public void init() throws IOException {
        imageDtos = imageService.findAllImages();
    }

    @Command
    public void doRedirectToView(@BindingParam("id") Long id) {
        // TODO: Check if not null
        Executions.sendRedirect("/view.zul?id=" + id);
    }
}

