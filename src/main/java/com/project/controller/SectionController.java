package com.project.controller;

import com.project.entity.Section;
import com.project.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/sections")
@PreAuthorize("hasRole('ADMIN')")
public class SectionController {
    @Autowired
    private SectionRepository sectionRepository;

    @GetMapping
    public List<Section> getAll() {
        return sectionRepository.findAll();
    }

    @PostMapping
    public Section create(@RequestBody Section section) {
        return sectionRepository.save(section);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Section> update(@PathVariable Long id, @RequestBody Section section) {
        return sectionRepository.findById(id)
                .map(s -> {
                    s.setSectionName(section.getSectionName());
                    s.setYear(section.getYear());
                    return ResponseEntity.ok(sectionRepository.save(s));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (sectionRepository.existsById(id)) {
            sectionRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
