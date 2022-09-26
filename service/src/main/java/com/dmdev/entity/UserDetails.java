package com.dmdev.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class UserDetails {

    @Id
    private Integer userId;
    private String firstname;
    private String lastname;
    private String patronymic;
    private String phone;
}
