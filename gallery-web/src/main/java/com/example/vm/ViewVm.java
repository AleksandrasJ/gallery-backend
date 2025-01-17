package com.example.vm;

import com.example.dto.ImageDto;
import com.example.service.ImageService;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

import java.io.IOException;

import static com.example.util.Utils.convertSetToString;

@Getter
@Setter
@VariableResolver(DelegatingVariableResolver.class)
public class ViewVm {

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
    public void doImageRemove() {
        imageService.deleteImage(imageDto.getId());
        Executions.sendRedirect("/index.zul");
    }

    @Command
    public void doImageEdit(@BindingParam("id") Long id) {
        Executions.sendRedirect("/edit.zul?id=" + id);
    }
}
