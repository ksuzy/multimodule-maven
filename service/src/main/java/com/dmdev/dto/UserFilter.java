package com.dmdev.dto;

import com.dmdev.database.entity.fields.Role;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserFilter {

    String email;
    String password;
    Role role;
    String firstname;
    String lastname;
    String patronymic;
    String phone;
}
