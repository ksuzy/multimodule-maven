package com.dmdev.converter;

import com.dmdev.entity.fields.Role;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Optional;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, Boolean> {
    @Override
    public Boolean convertToDatabaseColumn(Role role) {
        return Optional.ofNullable(role)
                .map(Role::isAdmin)
                .orElse(null);
    }

    @Override
    public Role convertToEntityAttribute(Boolean isAdmin) {
        return Optional.ofNullable(isAdmin)
                .map(Role::initRole)
                .orElse(null);
    }
}
