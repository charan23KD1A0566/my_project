package com.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/years")
@PreAuthorize("hasRole('ADMIN')")
public class YearController {
    private final Set<Integer> years = new HashSet<>(Arrays.asList(1, 2, 3, 4));

    @GetMapping
    public List<Integer> getAll() {
        return new ArrayList<>(years);
    }

    @PostMapping
    public ResponseEntity<?> addYear(@RequestBody Integer year) {
        if (years.contains(year)) {
            return ResponseEntity.badRequest().body("Duplicate year");
        }
        years.add(year);
        return ResponseEntity.ok(year);
    }

    @DeleteMapping("/{year}")
    public ResponseEntity<?> deleteYear(@PathVariable Integer year) {
        if (years.remove(year)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
