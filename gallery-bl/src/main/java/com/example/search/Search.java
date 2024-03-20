package com.example.search;

import com.example.entity.ImageEntity;
import com.example.entity.TagEntity;
import com.example.specification.ImageSpecification;
import lombok.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class Search {
    private List<TagEntity> tags;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    public Specification<ImageEntity> isBetweenDates() {
        return ImageSpecification.hasDate(this.dateFrom, this.dateTo);
    }
}
