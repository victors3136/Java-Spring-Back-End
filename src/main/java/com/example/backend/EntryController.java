package com.example.backend;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@Validated
@CrossOrigin(origins = "*")
public class EntryController {
    private final List<Entry> list = new ArrayList<>();

    private Optional<Entry> findByID(UUID id) {
        return list.stream()
                .filter(e -> (e.getId().equals(id)))
                .findFirst();
    }

    @GetMapping("/entry/{id}")
    public ResponseEntity<?> getOne(@PathVariable UUID id) {
        Optional<Entry> entry = findByID(id);
        return entry.isPresent() ? ResponseEntity.ok(entry.get()) :
                ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Invalid ID -- " + id);
    }

    @GetMapping("/entries")
    public ResponseEntity<List<Entry>> getAll() {
        return ResponseEntity.ok(list);
    }

    @PatchMapping("/entry/{id}")
    public ResponseEntity<?> patchOne(@PathVariable UUID id, @Valid @RequestBody Entry updatedEntry) {
        if (updatedEntry.validationFails()) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Entry> entry = findByID(id);
        return entry
                .map(foundEntry -> {
                    foundEntry.updateFields(updatedEntry);
                    return ResponseEntity.ok(foundEntry);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/entry")
    public ResponseEntity<UUID> postOne(@Valid @RequestBody Entry newEntry) {
        if (newEntry.validationFails()) {
            return ResponseEntity.badRequest().build();
        }
        do {
            newEntry.setId(UUID.randomUUID());
        } while (list.stream().anyMatch(x -> x.getId() == newEntry.getId()));
        list.add(newEntry);
        return new ResponseEntity<>(newEntry.getId(), HttpStatus.CREATED);
    }

    @DeleteMapping("/entry/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable UUID id) {
        return list.removeIf(entry -> entry.getId().equals(id)) ?
                ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }

    public boolean addEntry(Entry newEntry) {
        if(list.stream().anyMatch(entry -> entry.getId().equals(newEntry.getId())))
            return false;
        list.add(newEntry);
        return true;
    }
}

