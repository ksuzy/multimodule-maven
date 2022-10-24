package com.dmdev.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
@ToString(exclude = {"user"})
@Entity
public class UserAddress implements BaseEntity<Integer> {

    @Id
    @Column(name = "user_id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private User user;

    private String region;
    private String district;
    private String populationCenter;
    private String street;
    private String house;
    private Boolean isPrivate;
    private Integer frontDoor;
    private Integer floor;
    private Integer flat;

    public void setUser(User user) {
        this.user = user;
        this.id = user.getId();
    }
}
