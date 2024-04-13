package com.example.backend.controller;

import com.example.backend.model.Entry;
import com.example.backend.repository.InMemoryRepository;
import com.example.backend.repository.Repository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@Validated
@CrossOrigin(origins = "*")
public class EntryController {
    private final Repository<Entry> repository = new InMemoryRepository<>();

    @GetMapping("/entry/{id}")
    public ResponseEntity<?> getOne(@PathVariable UUID id) {
        Optional<Entry> entry = repository.getById(id);
        return entry.isPresent() ? ResponseEntity.ok(entry.get()) :
                ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Invalid ID -- " + id);
    }

    @GetMapping("/entries")
    public ResponseEntity<List<Entry>> getAll() {
        return ResponseEntity.ok(repository.getAll());
    }

    @PatchMapping("/entry/{id}")
    public ResponseEntity<?> patchOne(@PathVariable UUID id, @Valid @RequestBody Entry updatedEntry) {
        if (updatedEntry.validationFails()) {
            return ResponseEntity.badRequest().build();
        }
        return repository.patch(id, updatedEntry) ?
                ResponseEntity.ok(updatedEntry)
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/entry")
    public ResponseEntity<UUID> postOne(@Valid @RequestBody Entry newEntry) {
        if (newEntry.validationFails()) {
            return ResponseEntity.badRequest().build();
        }
        UUID newID = repository.post(newEntry);
        return new ResponseEntity<>(newID, HttpStatus.CREATED);
    }

    @DeleteMapping("/entry/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable UUID id) {
        return repository.delete(id) ?
                ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    public void addEntry(Entry newEntry) {
        repository.post(newEntry);
    }
}

