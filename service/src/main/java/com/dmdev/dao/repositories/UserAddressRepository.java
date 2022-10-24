package com.dmdev.dao.repositories;

import com.dmdev.entity.QUserAddress;
import com.dmdev.entity.UserAddress;

import javax.persistence.EntityManager;

public class UserAddressRepository extends BaseRepository<Integer, UserAddress> {
    public UserAddressRepository(EntityManager entityManager) {
        super(QUserAddress.userAddress, UserAddress.class, entityManager);
    }
}
