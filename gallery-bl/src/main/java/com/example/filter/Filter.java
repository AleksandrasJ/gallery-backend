package com.example.filter;

import com.example.entity.ImageEntity;
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
public class Filter {
    private List<Long> tagsIds;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    public Specification<ImageEntity> isBetweenDates() {
        return ImageSpecification.hasDate(this.dateFrom, this.dateTo);
    }
}
