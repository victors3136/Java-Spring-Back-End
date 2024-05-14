package com.example.backend.controllers;

import com.example.backend.model.Task;
import com.example.backend.repository.TaskRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
@RequestMapping("/task")
@CrossOrigin(origins = "*")
public class TaskController {
    private final TaskRepository taskRepository;

    @Autowired
    public TaskController(TaskRepository tasks) {
        taskRepository = tasks;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOneTask(@PathVariable UUID id) {
        System.out.println(MessageFormat.format("GET /task/{0}", id));
        Optional<Task> entry = taskRepository.findById(id);
        return entry.isPresent()
                ? ResponseEntity.ok(entry.get())
                : ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(MessageFormat.format("Invalid ID -- {0}", id));
    }

    @GetMapping("/count/{id}")
    public ResponseEntity<?> getSubtaskCount(@PathVariable UUID id) {
        System.out.println(MessageFormat.format("GET /task/count/{0}", id));
        return ResponseEntity.ok(taskRepository.countSubtasksByTask(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Task>> getAllTasks() {
        System.out.println("GET /task/all");
        return ResponseEntity.ok(taskRepository.findAll());
    }

    @GetMapping("/all/{id}")
    public ResponseEntity<Page<Task>> getTaskPage(@PathVariable int id) {
        System.out.println(MessageFormat.format("GET /task/all/{0}", id));
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }
        Page<Task> taskPage = taskRepository.findAll(PageRequest.of(id, 25));
        return ResponseEntity.ok(taskPage);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<?> patchOneTask(@PathVariable UUID id, @Valid @RequestBody @NotNull Task updatedTask) {
        System.out.println(MessageFormat.format("PATCH /task/{0}", id));
        System.out.println(MessageFormat.format("Body: {0}", updatedTask));

        if (updatedTask.validationFails()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Task> existingTaskOptional = taskRepository.findById(id);
        if (existingTaskOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Task existingTask = existingTaskOptional.get();
        existingTask.setName(updatedTask.getName());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setDueDate(updatedTask.getDueDate());
        Task savedTask = taskRepository.save(existingTask);

        return ResponseEntity.ok(savedTask);
    }


    @PostMapping("")
    public ResponseEntity<UUID> postOneTask(@Valid @RequestBody @NotNull Task newTask) {
        System.out.println("POST /task");
        System.out.println(MessageFormat.format("Body: {0}", newTask));
        if (newTask.validationFails()) {
            return ResponseEntity.badRequest().build();
        }
        var savedTask = taskRepository.save(newTask);
        return new ResponseEntity<>(savedTask.getId(), HttpStatus.CREATED);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOneTask(@PathVariable UUID id) {
        System.out.println(MessageFormat.format("DELETE /task/{0}", id));
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<?> deleteTasksBatch(@RequestBody @NotNull List<UUID> ids) {
        System.out.println("DELETE /task/batch");
        System.out.println(MessageFormat.format("Body: [{0}]", ids.stream().map(UUID::toString).reduce("", (s1, s2) -> s1 + ", " + s2)));
        List<Task> tasksToDelete = taskRepository.findAllById(ids);
        if (!tasksToDelete.isEmpty()) {
            taskRepository.deleteAll(tasksToDelete);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    public Task addTask(Task newTask) {
        return taskRepository.save(newTask);
    }

    public TaskRepository getTasksRepository() {
        return taskRepository;
    }
}

