package com.dmdev.database.dao.repositories;

import com.dmdev.database.dao.predicates.QPredicate;
import com.dmdev.database.entity.Book;
import com.dmdev.database.entity.QAuthor;
import com.dmdev.database.entity.QBook;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class BookRepository extends BaseRepository<Long, Book> {

    @Autowired
    public BookRepository(EntityManager entityManager) {
        super(QBook.book, Book.class, entityManager);
    }

    public List<Book> findAllByAuthorId(Integer id) {
        EntityManager entityManager = getEntityManager();

        Predicate predicate = QPredicate.builder()
                .add(id, QAuthor.author.id::eq)
                .buildAnd();

        return new JPAQuery<Book>(entityManager)
                .select(QBook.book)
                .from(QAuthor.author)
                .join(QAuthor.author.books, QBook.book)
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

