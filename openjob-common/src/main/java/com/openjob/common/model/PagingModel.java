package com.openjob.common.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Objects;

@Getter
@Setter
public class PagingModel {
    private int page;
    private int size;
    private String sort;

    public Pageable getPageable(){
        if (StringUtils.isEmpty(sort))
            return PageRequest.of(page, size);
        Sort.Direction direction;
        String[] sortCondition = sort.split(",");
        if (sortCondition.length > 1 && sortCondition[1].equalsIgnoreCase("desc"))
            direction = Sort.Direction.DESC;
        else
            direction = Sort.Direction.ASC;
        Sort sortObj = Sort.by(direction, sortCondition[0]);
        return  PageRequest.of(page, size, sortObj);

    }
}
