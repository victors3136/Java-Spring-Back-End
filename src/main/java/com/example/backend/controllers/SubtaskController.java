package com.example.backend.controllers;

import com.example.backend.model.Subtask;
import com.example.backend.repository.SubtaskRepository;
import com.example.backend.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
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
    private final SubtaskRepository subtaskRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public SubtaskController(SubtaskRepository subtasks, TaskRepository tasks) {
        subtaskRepository = subtasks;
        taskRepository = tasks;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOneSubtask(@PathVariable UUID id) {
        System.out.println(MessageFormat.format("GET /subtask/{0}", id));
        Optional<Subtask> entry = subtaskRepository.findById(id);
        return entry.isPresent()
                ? ResponseEntity.ok(entry.get())
                : ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(MessageFormat.format("Invalid ID -- {0}", id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Subtask>> getAllSubtasks() {
        System.out.println("GET /subtask/all");
        return ResponseEntity.ok(subtaskRepository.findAll());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchOneSubtask(@PathVariable UUID id, @Valid @RequestBody @NotNull Subtask updatedSubtask) {
        System.out.println(MessageFormat.format("PATCH /subtask/{0}", id));
        System.out.println(MessageFormat.format("Body: {0}", updatedSubtask));
        if (updatedSubtask.validationFails()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            taskRepository.getReferenceById(updatedSubtask.getTask());
        } catch (EntityNotFoundException _discard) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Subtask> existingSubtaskOptional = subtaskRepository.findById(id);
        if (existingSubtaskOptional.isPresent()) {
            Subtask existingSubtask = existingSubtaskOptional.get();
            existingSubtask.setSubject(updatedSubtask.getSubject());
            existingSubtask.setTask(updatedSubtask.getTask());
            Subtask savedSubtask = subtaskRepository.save(existingSubtask);
            return ResponseEntity.ok(savedSubtask);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("")
    public ResponseEntity<UUID> postOneSubtask(@Valid @RequestBody @NotNull Subtask newSubtask) {
        System.out.println("POST /subtask");
        System.out.println(MessageFormat.format("Body: {0}", newSubtask));
        if (newSubtask.validationFails()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            taskRepository.getReferenceById(newSubtask.getTask());
        } catch (EntityNotFoundException _discard) {
            return ResponseEntity.badRequest().build();
        }
        var savedSubtask = subtaskRepository.save(newSubtask);
        return new ResponseEntity<>(savedSubtask.getId(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOneSubtask(@PathVariable UUID id) {
        System.out.println(MessageFormat.format("DELETE /subtask/{0}", id));
        if (subtaskRepository.existsById(id)) {
            subtaskRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by_parent/{id}")
    public ResponseEntity<List<Subtask>> getSubtasksByParentId(@PathVariable UUID id) {
        System.out.println(MessageFormat.format("GET /subtask/by_parent/{0}", id));
        List<Subtask> subtasks = subtaskRepository.findByTask(id);
        return ResponseEntity.ok(subtasks);
    }

    public void addSubtask(Subtask newSubtask) {
        subtaskRepository.save(newSubtask);
    }

    public TaskRepository getTasksRepository() {
        return taskRepository;
    }
}
