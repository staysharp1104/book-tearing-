package com.webbook.repository;

import com.webbook.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    List<ChatHistory> findByBookIdOrderByCreateTimeAsc(String bookId);

    void deleteByBookId(String bookId);
}
