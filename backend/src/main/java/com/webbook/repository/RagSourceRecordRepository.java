package com.webbook.repository;

import com.webbook.entity.RagSourceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RagSourceRecordRepository extends JpaRepository<RagSourceRecord, Long> {
    List<RagSourceRecord> findByChatMessageIdOrderByRankAsc(Long chatMessageId);

    void deleteByBookId(String bookId);
}
