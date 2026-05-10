package com.booknest.book.controller;

import com.booknest.book.entity.Book;
import com.booknest.book.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/books")
@Tag(name = "Books & Catalog", description = "Book catalog management — browse, search, filter, and admin CRUD operations")
public class BookController {

    @Autowired
    private BookService bookService;

    // ─── CREATE ───────────────────────────────────────────────────────────────

    @Operation(summary = "Add a new book (Admin)",
               description = "Creates a new book listing in the catalog. Admin only.",
               security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Book created",
                     content = @Content(schema = @Schema(implementation = Book.class))),
        @ApiResponse(responseCode = "400", description = "Invalid book data", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        return new ResponseEntity<>(bookService.addBook(book), HttpStatus.CREATED);
    }

    // ─── READ ─────────────────────────────────────────────────────────────────

    @Operation(summary = "Get all books", description = "Returns the complete book catalog. Available to guests.")
    @ApiResponse(responseCode = "200", description = "List of all books")
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @Operation(summary = "Get book by ID", description = "Returns a single book by its database ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Book found",
                     content = @Content(schema = @Schema(implementation = Book.class))),
        @ApiResponse(responseCode = "404", description = "Book not found", content = @Content)
    })
    @GetMapping("/{bookId}")
    public ResponseEntity<Book> getBookById(
            @Parameter(description = "Book ID", example = "1") @PathVariable Long bookId) {
        return bookService.getBookById(bookId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get book by ISBN")
    @ApiResponse(responseCode = "200", description = "Book found")
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(
            @Parameter(description = "ISBN number", example = "978-0134685991") @PathVariable String isbn) {
        return bookService.getBookByIsbn(isbn)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Search books by keyword",
               description = "Full-text search across title, author, ISBN, and description fields.")
    @ApiResponse(responseCode = "200", description = "Matching books returned")
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(
            @Parameter(description = "Search keyword", example = "clean code") @RequestParam String keyword) {
        return ResponseEntity.ok(bookService.searchBooks(keyword));
    }

    @Operation(summary = "Filter books by genre")
    @ApiResponse(responseCode = "200", description = "Books in the specified genre")
    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Book>> getByGenre(
            @Parameter(description = "Genre name", example = "Fiction") @PathVariable String genre) {
        return ResponseEntity.ok(bookService.getByGenre(genre));
    }

    @Operation(summary = "Filter books by author")
    @ApiResponse(responseCode = "200", description = "Books by the specified author")
    @GetMapping("/author/{author}")
    public ResponseEntity<List<Book>> getByAuthor(
            @Parameter(description = "Author name", example = "Robert C. Martin") @PathVariable String author) {
        return ResponseEntity.ok(bookService.getByAuthor(author));
    }

    @Operation(summary = "Filter books by price range",
               description = "Returns books whose price falls between min and max (inclusive).")
    @ApiResponse(responseCode = "200", description = "Books in price range")
    @GetMapping("/price-range")
    public ResponseEntity<List<Book>> getByPriceRange(
            @Parameter(description = "Minimum price (₹)", example = "100") @RequestParam double min,
            @Parameter(description = "Maximum price (₹)", example = "500") @RequestParam double max) {
        return ResponseEntity.ok(bookService.getByPriceRange(min, max));
    }

    @Operation(summary = "Get featured books", description = "Returns books marked as featured for homepage display.")
    @ApiResponse(responseCode = "200", description = "Featured book list")
    @GetMapping("/featured")
    public ResponseEntity<List<Book>> getFeaturedBooks() {
        return ResponseEntity.ok(bookService.getFeaturedBooks());
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────

    @Operation(summary = "Update book details (Admin)",
               description = "Updates all fields of a book. Admin only.",
               security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "Book updated",
                 content = @Content(schema = @Schema(implementation = Book.class)))
    @PutMapping("/{bookId}")
    public ResponseEntity<Book> updateBook(
            @Parameter(description = "Book ID", example = "1") @PathVariable Long bookId,
            @RequestBody Book book) {
        book.setBookId(bookId);
        return ResponseEntity.ok(bookService.updateBook(book));
    }

    @Operation(summary = "Update stock level (Admin)",
               description = "Updates only the stock quantity for a book. Body: `{ \"stock\": 50 }`",
               security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "Stock updated")
    @PatchMapping("/{bookId}/stock")
    public ResponseEntity<String> updateStock(
            @Parameter(description = "Book ID", example = "1") @PathVariable Long bookId,
            @RequestBody Map<String, Integer> body) {
        bookService.updateStock(bookId, body.get("stock"));
        return ResponseEntity.ok("Stock updated successfully for bookId: " + bookId);
    }

    @Operation(summary = "Update book rating (System)",
               description = "Updates the average rating for a book. Called internally by review-service. Body: `{ \"rating\": 4.5 }`")
    @ApiResponse(responseCode = "200", description = "Rating updated")
    @PatchMapping("/{bookId}/rating")
    public ResponseEntity<String> updateRating(
            @Parameter(description = "Book ID", example = "1") @PathVariable Long bookId,
            @RequestBody Map<String, Double> body) {
        bookService.updateRating(bookId, body.get("rating"));
        return ResponseEntity.ok("Rating updated for bookId: " + bookId);
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    @Operation(summary = "Delete a book (Admin)",
               description = "Permanently removes a book from the catalog. Admin only.",
               security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Book deleted"),
        @ApiResponse(responseCode = "404", description = "Book not found", content = @Content)
    })
    @DeleteMapping("/{bookId}")
    public ResponseEntity<String> deleteBook(
            @Parameter(description = "Book ID", example = "1") @PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.ok("Book deleted successfully.");
    }
}
