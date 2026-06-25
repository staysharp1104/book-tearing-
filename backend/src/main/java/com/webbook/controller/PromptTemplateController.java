package com.webbook.controller;

import com.webbook.dto.PromptTemplateDTO;
import com.webbook.service.PromptTemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prompts")
public class PromptTemplateController {

    private final PromptTemplateService service;

    public PromptTemplateController(PromptTemplateService service) {
        this.service = service;
    }

    // List all templates
    @GetMapping
    public ResponseEntity<List<PromptTemplateDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // Get single template
    @GetMapping("/{id}")
    public ResponseEntity<PromptTemplateDTO> getById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get quick buttons (enabled)
    @GetMapping("/quick-buttons")
    public ResponseEntity<List<PromptTemplateDTO>> getQuickButtons() {
        return ResponseEntity.ok(service.findQuickButtons());
    }

    // Create template
    @PostMapping
    public ResponseEntity<PromptTemplateDTO> create(@RequestBody PromptTemplateDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    // Update template
    @PutMapping("/{id}")
    public ResponseEntity<PromptTemplateDTO> update(@PathVariable Integer id, @RequestBody PromptTemplateDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // Delete template
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    // Toggle enabled status
    @PutMapping("/{id}/toggle")
    public ResponseEntity<PromptTemplateDTO> toggle(@PathVariable Integer id) {
        return ResponseEntity.ok(service.toggleEnabled(id));
    }

    // Export single template
    @GetMapping("/export/{id}")
    public ResponseEntity<PromptTemplateDTO> exportOne(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Export all templates
    @GetMapping("/export")
    public ResponseEntity<List<PromptTemplateDTO>> exportAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // Import templates
    @PostMapping("/import")
    public ResponseEntity<Void> importTemplates(@RequestBody List<PromptTemplateDTO> templates) {
        service.importTemplates(templates);
        return ResponseEntity.ok().build();
    }

    // Init default templates (call once)
    @PostMapping("/init-defaults")
    public ResponseEntity<Void> initDefaults() {
        // Check if system templates already exist
        var existing = service.findAll();
        boolean hasSystem = existing.stream().anyMatch(t -> Boolean.TRUE.equals(t.getIsSystem()));
        if (!hasSystem) {
            String[][] defaults = {
                    {"\u5267\u60c5\u7ed3\u6784\u62c6\u89e3", "\u62c6\u89e3\u5c0f\u8bf4\u5267\u60c5\u4e3b\u7ebf\u3001\u652f\u7ebf\u3001\u8282\u594f", "analysis", "\u8bf7\u5206\u6790\u8fd9\u672c\u5c0f\u8bf4\u7684\u5267\u60c5\u7ed3\u6784\uff0c\u5305\u62ec\uff1a1\uff09\u4e3b\u7ebf\u5267\u60c5\u68b3\u7406 2\uff09\u652f\u7ebf\u5267\u60c5\u5206\u6790 3\uff09\u5267\u60c5\u8282\u594f\u8bc4\u4ef7 4\uff09\u51b2\u7a81\u8bbe\u8ba1 5\uff09\u9ad8\u6f6e\u4e0e\u4f4e\u8c37\u5206\u5e03", "1"},
            };
            for (String[] def : defaults) {
                PromptTemplateDTO dto = new PromptTemplateDTO();
                dto.setName(def[0]);
                dto.setDescription(def[1]);
                dto.setScene(def[2]);
                dto.setContent(def[3]);
                dto.setIsQuickBtn(true);
                dto.setIsSystem(true);
                dto.setEnabled(true);
                dto.setSortOrder(Integer.parseInt(def[4]));
                service.create(dto);
            }
        }
        return ResponseEntity.ok().build();
    }
}
