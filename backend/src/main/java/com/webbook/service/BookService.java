package com.webbook.service;

import com.webbook.entity.Book;
import com.webbook.repository.BookRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Book> findById(String bookId) {
        return bookRepository.findById(bookId);
    }

    public Page<Book> search(String category, String source, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Specification<Book> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (category != null && !category.isEmpty() && !"all".equals(category)) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            if (source != null && !source.isEmpty() && !"all".equals(source)) {
                predicates.add(cb.equal(root.get("source"), source));
            }
            if (keyword != null && !keyword.isEmpty()) {
                String pattern = "%" + keyword.trim() + "%";
                Predicate titleLike = cb.like(root.get("title"), pattern);
                Predicate authorLike = cb.like(root.get("author"), pattern);
                predicates.add(cb.or(titleLike, authorLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return bookRepository.findAll(spec, pageable);
    }

    public List<String> getAllCategories() {
        return bookRepository.findAll()
                .stream()
                .map(Book::getCategory)
                .filter(c -> c != null && !c.isEmpty())
                .distinct()
                .sorted()
                .toList();
    }

    public List<String> getAllSources() {
        return bookRepository.findAll()
                .stream()
                .map(Book::getSource)
                .filter(s -> s != null && !s.isEmpty())
                .distinct()
                .sorted()
                .toList();
    }

    @Transactional
    public void deleteById(String bookId) {
        bookRepository.deleteById(bookId);
    }
}
