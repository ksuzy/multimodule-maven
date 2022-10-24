package com.dmdev.dao.repositories;

import com.dmdev.entity.QUserDetails;
import com.dmdev.entity.UserDetails;

import javax.persistence.EntityManager;

public class UserDetailsRepository extends BaseRepository<Integer, UserDetails> {
    public UserDetailsRepository(EntityManager entityManager) {
        super(QUserDetails.userDetails, UserDetails.class, entityManager);
    }
}
