package com.webbook.service;

import com.webbook.dto.PromptTemplateDTO;
import com.webbook.entity.PromptTemplate;
import com.webbook.repository.PromptTemplateRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PromptTemplateService {

    private final PromptTemplateRepository repository;

    public PromptTemplateService(PromptTemplateRepository repository) {
        this.repository = repository;
    }

    public List<PromptTemplateDTO> findAll() {
        return repository.findAllByOrderBySortOrderAsc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<PromptTemplateDTO> findById(Integer id) {
        return repository.findById(id).map(this::toDTO);
    }

    public List<PromptTemplateDTO> findQuickButtons() {
        return repository.findByIsQuickBtnAndEnabledOrderBySortOrderAsc(true, true).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PromptTemplateDTO create(PromptTemplateDTO dto) {
        PromptTemplate entity = new PromptTemplate();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setScene(dto.getScene());
        entity.setContent(dto.getContent());
        entity.setIsQuickBtn(dto.getIsQuickBtn() != null ? dto.getIsQuickBtn() : false);
        entity.setIsSystem(false);
        entity.setEnabled(true);
        entity.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return toDTO(repository.save(entity));
    }

    public PromptTemplateDTO update(Integer id, PromptTemplateDTO dto) {
        PromptTemplate entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("\u6a21\u677f\u4e0d\u5b58\u5728: " + id));

        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getScene() != null) entity.setScene(dto.getScene());
        if (dto.getContent() != null) entity.setContent(dto.getContent());
        if (dto.getIsQuickBtn() != null) entity.setIsQuickBtn(dto.getIsQuickBtn());
        if (dto.getEnabled() != null) entity.setEnabled(dto.getEnabled());
        if (dto.getSortOrder() != null) entity.setSortOrder(dto.getSortOrder());
        entity.setUpdatedAt(LocalDateTime.now());

        return toDTO(repository.save(entity));
    }

    public void delete(Integer id) {
        PromptTemplate entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("\u6a21\u677f\u4e0d\u5b58\u5728: " + id));
        if (Boolean.TRUE.equals(entity.getIsSystem())) {
            throw new IllegalArgumentException("\u7cfb\u7edf\u5185\u7f6e\u6a21\u677f\u4e0d\u53ef\u5220\u9664");
        }
        repository.deleteById(id);
    }

    public PromptTemplateDTO toggleEnabled(Integer id) {
        PromptTemplate entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("\u6a21\u677f\u4e0d\u5b58\u5728: " + id));
        entity.setEnabled(!entity.getEnabled());
        entity.setUpdatedAt(LocalDateTime.now());
        return toDTO(repository.save(entity));
    }

    public void importTemplates(List<PromptTemplateDTO> templates) {
        for (PromptTemplateDTO dto : templates) {
            PromptTemplate entity = new PromptTemplate();
            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());
            entity.setScene(dto.getScene());
            entity.setContent(dto.getContent());
            entity.setIsQuickBtn(dto.getIsQuickBtn() != null ? dto.getIsQuickBtn() : false);
            entity.setIsSystem(false);
            entity.setEnabled(true);
            entity.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            repository.save(entity);
        }
    }

    public PromptTemplateDTO toDTO(PromptTemplate entity) {
        PromptTemplateDTO dto = new PromptTemplateDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setScene(entity.getScene());
        dto.setContent(entity.getContent());
        dto.setIsQuickBtn(entity.getIsQuickBtn());
        dto.setIsSystem(entity.getIsSystem());
        dto.setEnabled(entity.getEnabled());
        dto.setSortOrder(entity.getSortOrder());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
