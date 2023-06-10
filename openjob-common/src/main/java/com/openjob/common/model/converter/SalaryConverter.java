package com.openjob.common.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openjob.common.model.SalaryModel;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class SalaryConverter implements AttributeConverter<SalaryModel, String> {
    @Override
    public String convertToDatabaseColumn(SalaryModel attribute) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            System.out.println("Fail to convert salaryModel!!!!!!!");
            throw new RuntimeException(e);
        }
    }

    @Override
    public SalaryModel convertToEntityAttribute(String dbData) {
        return null;
    }
}
