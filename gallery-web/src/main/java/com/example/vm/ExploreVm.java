package com.example.vm;

import com.example.dto.ImageDisplayDto;
import com.example.dto.TagDto;
import com.example.service.ImageService;
import com.example.service.TagService;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.example.util.Utils.convertDateToLocalDate;

@Getter
@Setter
@VariableResolver(DelegatingVariableResolver.class)
public class ExploreVm {

    @WireVariable
    ImageService imageService;
    @WireVariable
    TagService tagService;

    private String tagName;
    private Date fromDate;
    private Date toDate;

    private List<ImageDisplayDto> imageDtos;

    @Init
    public void init(@QueryParam("keyword") String keyword) throws IOException {
        tagName = "";
        if (keyword.isEmpty()) {
            imageDtos = imageService.findAllImages();
        } else {
            imageDtos = imageService.findAllImagesByKeyword(keyword);
        }
    }

    @Command
    public void doRedirectToView(@BindingParam("id") Long id) {
        // TODO: Check if not null
        Executions.sendRedirect("/view.zul?id=" + id);
    }

    @Command
    @NotifyChange("imageDtos")
    public void filterByTag() {
        if (!tagName.isEmpty() && fromDate == null && toDate == null) {
            TagDto tagDto = tagService.findTagByName(tagName);
            imageDtos = imageService.findAllImagesByTag(tagDto);
        }

        if (fromDate != null && toDate != null && tagName.isEmpty()) {
            imageDtos = imageService.findAllImagesByDate(convertDateToLocalDate(fromDate),
                    convertDateToLocalDate(toDate));
        }

        if (!tagName.isEmpty() && fromDate != null && toDate != null) {
            TagDto tagDto = tagService.findTagByName(tagName);
            imageDtos = imageService.findAllImagesByDateAndTag(tagDto,
                    convertDateToLocalDate(fromDate), convertDateToLocalDate(toDate));
        }
    }
}

