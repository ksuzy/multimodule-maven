package com.dmdev.database.dao.repositories;

import com.dmdev.database.entity.Author;
import com.dmdev.database.entity.QAuthor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class AuthorRepository extends BaseRepository<Integer, Author> {

    @Autowired
    public AuthorRepository(EntityManager entityManager) {
        super(QAuthor.author, Author.class, entityManager);
    }
}