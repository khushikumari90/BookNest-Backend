package com.booknest.book.service;

import com.booknest.book.entity.Book;
import com.booknest.book.exception.BookNotFoundException;
import com.booknest.book.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public Book addBook(Book book) {
        // Validate ISBN uniqueness
        if (book.getIsbn() != null && bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
            throw new RuntimeException("A book with ISBN " + book.getIsbn() + " already exists.");
        }
        return bookRepository.save(book);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Optional<Book> getBookById(Long bookId) {
        return bookRepository.findById(bookId);
    }

    @Override
    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    @Override
    public List<Book> searchBooks(String keyword) {
        return bookRepository.searchByKeyword(keyword);
    }

    @Override
    public List<Book> getByGenre(String genre) {
        return bookRepository.findByGenreIgnoreCase(genre);
    }

    @Override
    public List<Book> getByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    @Override
    public List<Book> getByPriceRange(double minPrice, double maxPrice) {
        return bookRepository.findByPriceBetween(minPrice, maxPrice);
    }

    @Override
    public Book updateBook(Book updatedBook) {
        Book existing = bookRepository.findById(updatedBook.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + updatedBook.getBookId()));

        existing.setTitle(updatedBook.getTitle());
        existing.setAuthor(updatedBook.getAuthor());
        existing.setIsbn(updatedBook.getIsbn());
        existing.setGenre(updatedBook.getGenre());
        existing.setPublisher(updatedBook.getPublisher());
        existing.setPrice(updatedBook.getPrice());
        existing.setStock(updatedBook.getStock());
        existing.setDescription(updatedBook.getDescription());
        existing.setCoverImageUrl(updatedBook.getCoverImageUrl());
        existing.setPublishedDate(updatedBook.getPublishedDate());
        existing.setFeatured(updatedBook.isFeatured());

        return bookRepository.save(existing);
    }

    @Override
    public void deleteBook(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("Book not found with id: " + bookId);
        }
        bookRepository.deleteById(bookId);
    }

    @Override
    public void updateStock(Long bookId, int quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + bookId));
        if (quantity < 0) {
            throw new RuntimeException("Stock quantity cannot be negative.");
        }
        book.setStock(quantity);
        bookRepository.save(book);
    }

    @Override
    public List<Book> getFeaturedBooks() {
        return bookRepository.findByFeaturedTrue();
    }

    @Override
    public void updateRating(Long bookId, double newRating) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + bookId));
        book.setRating(newRating);
        bookRepository.save(book);
    }
}
