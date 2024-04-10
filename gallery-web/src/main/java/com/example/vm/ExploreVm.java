package com.example.vm;

import com.example.dto.ImageDisplayDto;
import com.example.filter.Filter;
import com.example.service.ImageService;
import com.example.service.TagService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    private String keyword;
    private Filter filter;

    private int pageNumber;

    private Page<ImageDisplayDto> page;
    private List<ImageDisplayDto> imageDisplayDtos;

    // TODO: Sutvarkyt disabled
    private boolean next = false;
    private boolean previous = false;

    @Init
    public void init(@QueryParam("keyword") String keyword) throws IOException {
        tagName = "";
        fromDate = Date.from(LocalDate.of(2000, 1, 1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        toDate = new Date();

        this.keyword = keyword;

        pageNumber = 0;

        Pageable pageable = PageRequest.of(pageNumber, 8);

        if (keyword.isEmpty()) {
            page = imageService.findAllImages(pageable);
        } else {
            page = imageService.findAllImagesByKeyword(pageable, keyword);
        }
        imageDisplayDtos = page.getContent();
    }

    @Command
    public void doRedirectToView(@BindingParam("id") Long id) {
        if (imageService.findImageById(id) != null) {
            Executions.sendRedirect("/view.zul?id=" + id);
        }
    }

    @Command
    @NotifyChange("imageDisplayDtos")
    public void doFiltering() {
        List<String> stringList = Arrays.stream(tagName.split(", "))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toList());

        List<Long> tagsIds = new ArrayList<>();

        if (!stringList.isEmpty()) {
            tagsIds = stringList.stream()
                    .map(tagName -> tagService.findTagByName(tagName).getId())
                    .collect(Collectors.toList());
        }

        filter = new Filter();

        filter.setTagsIds(tagsIds);
        filter.setDateFrom(convertDateToLocalDate(fromDate));
        filter.setDateTo(convertDateToLocalDate(toDate));

        pageNumber = 0;

        Pageable pageable = PageRequest.of(pageNumber, 8);

        page = imageService.filterImage(filter, pageable);
        imageDisplayDtos = page.getContent();
    }

    @Command
    @NotifyChange({"imageDisplayDtos", "next"})
    public void doPageChangeForward() {
        if (!keyword.isEmpty()) {
            if (page.hasNext()) {
                ++pageNumber;
                Pageable pageable = PageRequest.of(pageNumber, 8);
                page = imageService.findAllImagesByKeyword(pageable, keyword);
                imageDisplayDtos = page.getContent();
            }
        } else if (filter != null) {
            if (page.hasNext()) {
                ++pageNumber;
                Pageable pageable = PageRequest.of(pageNumber, 8);
                page = imageService.filterImage(filter, pageable);
                imageDisplayDtos = page.getContent();
            }
        } else {
            if (page.hasNext()) {
                ++pageNumber;
                Pageable pageable = PageRequest.of(pageNumber, 8);
                page = imageService.findAllImages(pageable);
                imageDisplayDtos = page.getContent();
            }
        }
    }

    @Command
    @NotifyChange({"imageDisplayDtos", "previous"})
    public void doPageChangeBack() {
        if (!keyword.isEmpty()) {
            if (!page.isFirst()) {
                --pageNumber;
                Pageable pageable = PageRequest.of(pageNumber, 8);
                page = imageService.findAllImagesByKeyword(pageable, keyword);
                imageDisplayDtos = page.getContent();
            }
        } else if (filter != null) {
            if (!page.isFirst()) {
                --pageNumber;
                Pageable pageable = PageRequest.of(pageNumber, 8);
                page = imageService.filterImage(filter, pageable);
                imageDisplayDtos = page.getContent();
            }
        } else {
            if (!page.isFirst()) {
                --pageNumber;
                Pageable pageable = PageRequest.of(pageNumber, 8);
                page = imageService.findAllImages(pageable);
                imageDisplayDtos = page.getContent();
            }
        }
    }
}

