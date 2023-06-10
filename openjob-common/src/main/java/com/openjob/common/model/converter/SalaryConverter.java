package com.openjob.common.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openjob.common.model.SalaryModel;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class SalaryConverter implements AttributeConverter<SalaryModel, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public String convertToDatabaseColumn(SalaryModel attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            System.out.println("Fail to convert SalaryModel to String!!!!!!!");
            throw new RuntimeException(e);
        }
    }

    @Override
    public SalaryModel convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, SalaryModel.class);
        } catch (JsonProcessingException e) {
            System.out.println("Fail to convert String to SalaryModel!!!!!!!");
            throw new RuntimeException(e);
        }
    }
}
