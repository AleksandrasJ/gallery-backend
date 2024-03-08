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
    private int pageNumber;

    private List<ImageDisplayDto> imageDtos;

    @Init
    public void init(@QueryParam("keyword") String keyword) throws IOException {
        tagName = "";
        pageNumber = 0;
        imageDtos = keyword.isEmpty() ?
                imageService.findAllImages(pageNumber, 8) : imageService.findAllImagesByKeyword(keyword);
    }

    @Command
    public void doRedirectToView(@BindingParam("id") Long id) {
        if (imageService.findImageById(id) != null) {
            Executions.sendRedirect("/view.zul?id=" + id);
        }
    }

    @Command
    @NotifyChange("imageDtos")
    public void doFilterByTag() {
        if (!tagName.isEmpty() && fromDate == null && toDate == null) {
            TagDto tagDto = tagService.findTagByName(tagName.trim());
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

    @Command
    @NotifyChange("imageDtos")
    public void doPageChangeForward() {
        if (imageDtos.size() == 8) {
            pageNumber += 8;
            imageDtos = imageService.findAllImages(pageNumber, 8);
        }
    }

    @Command
    @NotifyChange("imageDtos")
    public void doPageChangeBack() {
        if (pageNumber <= 0) {
            pageNumber = 0;
        } else {
            pageNumber -= 8;
            imageDtos = imageService.findAllImages(pageNumber, 8);
        }
    }
}

