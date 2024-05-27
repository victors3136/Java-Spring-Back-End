package com.example.backend.controllers;

import com.example.backend.model.Subtask;
import com.example.backend.service.SubtaskService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@Validated
@CrossOrigin(origins = "*")
@RequestMapping("/subtask")
public class SubtaskController {
    private final SubtaskService subtaskService;

    @Autowired
    public SubtaskController(SubtaskService subtaskService) {
        this.subtaskService = subtaskService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOneSubtask(@PathVariable UUID id) {
        System.out.println(MessageFormat.format("GET /subtask/{0}", id));
        Optional<Subtask> entry = subtaskService.getById(id);
        return entry.isPresent()
                ? ResponseEntity.ok(entry.get())
                : ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(MessageFormat.format("Invalid ID -- {0}", id));
    }

    @GetMapping("/count/{id}")
    public ResponseEntity<?> getSubtaskCount(@PathVariable UUID id) {
        System.out.println(MessageFormat.format("GET /subtask/count/{0}", id));
        return ResponseEntity.ok(subtaskService.countSubtasksByTask(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Subtask>> getAllSubtasks() {
        System.out.println("GET /subtask/all");
        return ResponseEntity.ok(subtaskService.getAll().stream().toList());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchOneSubtask(@PathVariable UUID id, @Valid @RequestBody @NotNull Subtask updatedSubtask) {
        System.out.println(MessageFormat.format("PATCH /subtask/{0}", id));
        System.out.println(MessageFormat.format("Body: {0}", updatedSubtask));
        if (updatedSubtask.validationFails() || !subtaskService.tryToUpdate(id, updatedSubtask)) {
            return ResponseEntity.badRequest().build();
        }
        var subtask = subtaskService.getById(id);
        return subtask.isPresent()
                ? ResponseEntity.ok(subtask.get())
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<UUID> postOneSubtask(@Valid @RequestBody @NotNull Subtask newSubtask) {
        System.out.println("POST /subtask");
        System.out.println(MessageFormat.format("Body: {0}", newSubtask));
        if (newSubtask.validationFails()) {
            return ResponseEntity.badRequest().build();
        }

        Subtask savedSubtask = subtaskService.save(newSubtask);
        return new ResponseEntity<>(savedSubtask.getId(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOneSubtask(@PathVariable UUID id) {
        System.out.println(MessageFormat.format("DELETE /subtask/{0}", id));
        return subtaskService.tryToDelete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/for/{id}")
    public ResponseEntity<List<Subtask>> getSubtasksByParentId(@PathVariable UUID id) {
        System.out.println(MessageFormat.format("GET /subtask/by_parent/{0}", id));
        return ResponseEntity.ok(subtaskService.getForTask(id).stream().toList());
    }
}
