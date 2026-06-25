package com.webbook.controller;

import com.webbook.entity.Book;
import com.webbook.entity.Chapter;
import com.webbook.service.BookService;
import com.webbook.service.ChapterService;
import com.webbook.service.RagIndexService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final ChapterService chapterService;
    private final RagIndexService ragIndexService;

    public BookController(BookService bookService, ChapterService chapterService,
                          RagIndexService ragIndexService) {
        this.bookService = bookService;
        this.chapterService = chapterService;
        this.ragIndexService = ragIndexService;
    }

    @GetMapping
    public ResponseEntity<?> getAllBooks(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        // If no filter params, return all (backward compatible)
        if (category == null && source == null && keyword == null) {
            return ResponseEntity.ok(bookService.findAll());
        }

        Page<Book> result = bookService.search(category, source, keyword, page, size);
        return ResponseEntity.ok(Map.of(
                "content", result.getContent(),
                "totalElements", result.getTotalElements(),
                "totalPages", result.getTotalPages(),
                "number", result.getNumber(),
                "size", result.getSize()
        ));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(bookService.getAllCategories());
    }

    @GetMapping("/sources")
    public ResponseEntity<List<String>> getSources() {
        return ResponseEntity.ok(bookService.getAllSources());
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<Book> getBook(@PathVariable String bookId) {
        return bookService.findById(bookId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{bookId}/chapters")
    public ResponseEntity<List<Chapter>> getChapters(@PathVariable String bookId) {
        return ResponseEntity.ok(chapterService.findByBookId(bookId));
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable String bookId) {
        // Clean up RAG cache first
        ragIndexService.clearIndex(bookId);
        // Delete book
        bookService.deleteById(bookId);
        return ResponseEntity.ok().build();
    }
}
