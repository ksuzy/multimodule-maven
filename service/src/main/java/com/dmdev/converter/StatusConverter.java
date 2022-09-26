package com.dmdev.converter;

import com.dmdev.entity.fields.Status;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Optional;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {
    @Override
    public String convertToDatabaseColumn(Status status) {
        return Optional.ofNullable(status)
                .map(Status::name)
                .orElse(null);
    }

    @Override
    public Status convertToEntityAttribute(String s) {
        return Optional.ofNullable(s)
                .map(Status::valueOf)
                .orElse(null);
    }
}
