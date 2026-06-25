package com.webbook.repository;

import com.webbook.entity.RankBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankBookRepository extends JpaRepository<RankBook, Integer> {
}
