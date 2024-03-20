package com.example.vm;

import com.example.dto.FilterDto;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.util.Utils.convertDateToLocalDate;
import static com.example.util.Utils.convertStringToSet;

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

    private FilterDto filter;
    private List<ImageDisplayDto> imageDtos;

    @Init
    public void init(@QueryParam("keyword") String keyword) throws IOException {
        tagName = "";
        fromDate = Date.from(LocalDate.of(2000, 1, 1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        toDate = new Date();
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
    public void doFiltering() {
        List<TagDto> tags = new ArrayList<>(convertStringToSet(tagName));
        filter = new FilterDto();

        filter.setTags(tags);
        filter.setDateFrom(convertDateToLocalDate(fromDate));
        filter.setDateTo(convertDateToLocalDate(toDate));

        pageNumber = 0;
        imageDtos = imageService.filterImage(filter, pageNumber, 8);
    }

    @Command
    @NotifyChange("imageDtos")
    public void doPageChangeForward() {
        if (filter == null) {
            if (imageDtos.size() == 8) {
                pageNumber += 8;
                imageDtos = imageService.findAllImages(pageNumber, 8);
            }
        } else {
            if (imageDtos.size() == 8) {
                pageNumber += 8;
                imageDtos = imageService.filterImage(filter, pageNumber, 8);
            }
        }
    }

    @Command
    @NotifyChange("imageDtos")
    public void doPageChangeBack() {
        if (filter == null) {
            if (pageNumber <= 0) {
                pageNumber = 0;
            } else {
                pageNumber -= 8;
                imageDtos = imageService.findAllImages(pageNumber, 8);
            }
        } else {
            if (pageNumber <= 0) {
                pageNumber = 0;
            } else {
                pageNumber -= 8;
                imageDtos = imageService.filterImage(filter, pageNumber, 8);
            }
        }
    }
}

