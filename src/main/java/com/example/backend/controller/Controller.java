package com.example.backend.controller;

import com.example.backend.model.Subtask;
import com.example.backend.model.Tasks;
import com.example.backend.repository.SubtaskRepository;
import com.example.backend.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@Validated
@CrossOrigin(origins = "*")
public class Controller {
    private final TaskRepository taskRepository;
    private final SubtaskRepository subtaskRepository;

    @Autowired
    public Controller(TaskRepository tasks, SubtaskRepository subtasks) {
        taskRepository = tasks;
        subtaskRepository = subtasks;
    }

    @GetMapping("/task/{id}")
    public ResponseEntity<?> getOneTask(@PathVariable UUID id) {
        Optional<Tasks> entry = Optional.of(taskRepository.getReferenceById(id));
        return entry.isPresent() ?
                ResponseEntity.ok(entry.get())
                : ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Invalid ID -- " + id);
    }

    @GetMapping("/subtask/{id}")
    public ResponseEntity<?> getOneSubtask(@PathVariable UUID id) {
        Optional<Subtask> entry = Optional.of(subtaskRepository.getReferenceById(id));
        return entry.isPresent() ?
                ResponseEntity.ok(entry.get())
                : ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Invalid ID -- " + id);
    }

    @GetMapping("/task/all")
    public ResponseEntity<List<Tasks>> getAllTasks() {
        return ResponseEntity.ok(taskRepository.findAll());
    }

    @GetMapping("/subtask/all")
    public ResponseEntity<List<Subtask>> getAllSubtasks() {
        return ResponseEntity.ok(subtaskRepository.findAll());
    }

    @PatchMapping("/task/{id}")
    public ResponseEntity<?> patchOneTask(@PathVariable UUID id, @Valid @RequestBody Tasks updatedTask) {
        if (updatedTask.validationFails()) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Tasks> existingTaskOptional = taskRepository.findById(id);
        if (existingTaskOptional.isPresent()) {
            Tasks existingTask = existingTaskOptional.get();
            existingTask.setName(updatedTask.getName());
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setPriority(updatedTask.getPriority());
            existingTask.setDueDate(updatedTask.getDueDate());
            Tasks savedTask = taskRepository.save(existingTask);
            return ResponseEntity.ok(savedTask);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/subtask/{id}")
    public ResponseEntity<?> patchOneSubtask(@PathVariable UUID id, @Valid @RequestBody Subtask updatedSubtask) {
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

    @PostMapping("/task")
    public ResponseEntity<UUID> postOneTask(@Valid @RequestBody Tasks newTask) {
        if (newTask.validationFails()) {
            return ResponseEntity.badRequest().build();
        }
        Tasks savedTask = taskRepository.save(newTask);
        return new ResponseEntity<>(savedTask.getId(), HttpStatus.CREATED);
    }

    @PostMapping("/subtask")
    public ResponseEntity<UUID> postOneSubtask(@Valid @RequestBody Subtask newSubtask) {
        if (newSubtask.validationFails()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            taskRepository.getReferenceById(newSubtask.getTask());
        } catch (EntityNotFoundException _discard) {
            return ResponseEntity.badRequest().build();
        }
        Subtask savedSubtask = subtaskRepository.save(newSubtask);
        return new ResponseEntity<>(savedSubtask.getId(), HttpStatus.CREATED);
    }

    @DeleteMapping("/task/{id}")
    public ResponseEntity<?> deleteOneTask(@PathVariable UUID id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/subtask/{id}")
    public ResponseEntity<?> deleteOneSubtask(@PathVariable UUID id) {
        if (subtaskRepository.existsById(id)) {
            subtaskRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/subtask/by_parent/{id}")
    public ResponseEntity<List<Subtask>> getSubtasksByParentId(@PathVariable UUID id) {
        List<Subtask> subtasks = subtaskRepository.findByTask(id);
        return ResponseEntity.ok(subtasks);
    }

    public void addTask(Tasks newTask) {
        taskRepository.save(newTask);
    }

    @SuppressWarnings("unused")
    public void addSubtask(Subtask newSubtask) {
        subtaskRepository.save(newSubtask);
    }

    public TaskRepository getTasksRepository() {
        return taskRepository;
    }
}

