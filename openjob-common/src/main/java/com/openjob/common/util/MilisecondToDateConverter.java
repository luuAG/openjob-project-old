package com.openjob.common.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MilisecondToDateConverter implements Converter<Long, Date> {
    @Override
    public Date convert(Long source) {
        return new Date(source);
    }
}
