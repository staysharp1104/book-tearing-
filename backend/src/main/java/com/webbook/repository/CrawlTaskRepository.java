package com.webbook.repository;

import com.webbook.entity.CrawlTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrawlTaskRepository extends JpaRepository<CrawlTask, Long> {
}
