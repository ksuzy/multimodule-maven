package com.dmdev.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class UserAddress {

    @Id
    private Integer userId;
    private String region;
    private String district;
    private String populationCenter;
    private String street;
    private String house;
    private Boolean isPrivate;
    private String frontDoor;
    private String floor;
    private String flat;

}
