package com.dmdev.dao.repositories;

import com.dmdev.entity.Author;
import com.dmdev.entity.QAuthor;

import javax.persistence.EntityManager;

public class AuthorRepository extends BaseRepository<Integer, Author> {

    public AuthorRepository(EntityManager entityManager) {
        super(QAuthor.author, Author.class, entityManager);
    }
}