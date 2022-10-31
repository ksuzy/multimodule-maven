package com.dmdev.database.dao.repositories;

import com.dmdev.database.entity.QUserDetails;
import com.dmdev.database.entity.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class UserDetailsRepository extends BaseRepository<Integer, UserDetails> {

    @Autowired
    public UserDetailsRepository(EntityManager entityManager) {
        super(QUserDetails.userDetails, UserDetails.class, entityManager);
    }
}