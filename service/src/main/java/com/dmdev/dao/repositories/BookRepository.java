package com.dmdev.dao.repositories;

import com.dmdev.dao.predicates.QPredicate;
import com.dmdev.entity.Author;
import com.dmdev.entity.Book;
import com.dmdev.entity.QBook;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;

import javax.persistence.EntityManager;
import java.util.List;

public class BookRepository extends BaseRepository<Long, Book> {

    public BookRepository(EntityManager entityManager) {
        super(QBook.book, Book.class, entityManager);
    }

    public List<Book> findAllByAuthor(Author author) {
        EntityManager entityManager = getEntityManager();

        Predicate predicate = QPredicate.builder()
                .add(author, QBook.book.authors::contains)
                .buildAnd();

        return new JPAQuery<Book>(entityManager)
                .select(QBook.book)
                .from(QBook.book)
                .where(predicate)
                .fetch();
    }

    public List<Book> findAllByIssueYear(Short issueYear) {
        EntityManager entityManager = getEntityManager();

        Predicate predicate = QPredicate.builder()
                .add(issueYear, QBook.book.issueYear::eq)
                .buildAnd();

        return new JPAQuery<Book>(entityManager)
                .select(QBook.book)
                .from(QBook.book)
                .where(predicate)
                .fetch();
    }
}

