package com.webbook.repository;

import com.webbook.entity.PromptTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromptTemplateRepository extends JpaRepository<PromptTemplate, Integer> {
    List<PromptTemplate> findAllByOrderBySortOrderAsc();

    List<PromptTemplate> findByIsQuickBtnAndEnabledOrderBySortOrderAsc(boolean isQuickBtn, boolean enabled);

    boolean existsByIsSystemTrueAndId(Integer id);
}
