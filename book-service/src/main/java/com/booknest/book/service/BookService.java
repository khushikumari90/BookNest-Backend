package com.booknest.book.service;

import com.booknest.book.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {

    Book addBook(Book book);

    List<Book> getAllBooks();

    Optional<Book> getBookById(Long bookId);

    Optional<Book> getBookByIsbn(String isbn);

    List<Book> searchBooks(String keyword);

    List<Book> getByGenre(String genre);

    List<Book> getByAuthor(String author);

    List<Book> getByPriceRange(double minPrice, double maxPrice);

    Book updateBook(Book book);

    void deleteBook(Long bookId);

    void updateStock(Long bookId, int quantity);

    List<Book> getFeaturedBooks();

    void updateRating(Long bookId, double newRating);
}
