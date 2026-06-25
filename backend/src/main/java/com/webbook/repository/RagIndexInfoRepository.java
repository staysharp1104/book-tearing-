package com.webbook.repository;

import com.webbook.entity.RagIndexInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RagIndexInfoRepository extends JpaRepository<RagIndexInfo, String> {
    Optional<RagIndexInfo> findByBookId(String bookId);
}
