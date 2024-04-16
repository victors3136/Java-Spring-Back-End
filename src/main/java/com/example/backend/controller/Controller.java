package com.example.backend.controller;

import com.example.backend.model.Task;
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
public class Controller {
    private final Repository<Task> repository = new InMemoryRepository<>();

    @GetMapping("/entry/{id}")
    public ResponseEntity<?> getOne(@PathVariable UUID id) {
        Optional<Task> entry = repository.getById(id);
        return entry.isPresent() ? ResponseEntity.ok(entry.get()) :
                ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Invalid ID -- " + id);
    }

    @GetMapping("/entries")
    public ResponseEntity<List<Task>> getAll() {
        return ResponseEntity.ok(repository.getAll());
    }

    @PatchMapping("/entry/{id}")
    public ResponseEntity<?> patchOne(@PathVariable UUID id, @Valid @RequestBody Task updatedTask) {
        if (updatedTask.validationFails()) {
            return ResponseEntity.badRequest().build();
        }
        return repository.patch(id, updatedTask) ?
                ResponseEntity.ok(updatedTask)
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/entry")
    public ResponseEntity<UUID> postOne(@Valid @RequestBody Task newTask) {
        if (newTask.validationFails()) {
            return ResponseEntity.badRequest().build();
        }
        UUID newID = repository.post(newTask);
        return new ResponseEntity<>(newID, HttpStatus.CREATED);
    }

    @DeleteMapping("/entry/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable UUID id) {
        return repository.delete(id) ?
                ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    public void addEntry(Task newTask) {
        repository.post(newTask);
    }
}

