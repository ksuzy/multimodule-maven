package com.dmdev.database.dao.repositories;

import com.dmdev.database.entity.QUserAddress;
import com.dmdev.database.entity.UserAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class UserAddressRepository extends BaseRepository<Integer, UserAddress> {

    @Autowired
    public UserAddressRepository(EntityManager entityManager) {
        super(QUserAddress.userAddress, UserAddress.class, entityManager);
    }
}