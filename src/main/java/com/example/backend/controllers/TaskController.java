package com.example.backend.controllers;

import com.example.backend.model.Task;
import com.example.backend.service.TaskService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RestController
@Validated
@RequestMapping("/task")
@CrossOrigin(origins = "*")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOneTask(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        System.out.println(MessageFormat.format("GET /task/{0}", id));
        Optional<Task> task = taskService.getForTokenHolderById(id, token);
        return task.isPresent()
                ? ResponseEntity.ok(task.get())
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("That is not yours to see!");
    }


    @GetMapping("/all")
    public ResponseEntity<List<Task>> getAllTasks(@RequestHeader("Authorization") String ignoredToken) {
        System.out.println("GET /task/all");
        return ResponseEntity.ok(taskService.getAll().stream().toList());
    }

    @GetMapping("/all/{id}")
    public ResponseEntity<Page<Task>> getTaskPage(@PathVariable int id, @RequestHeader("Authorization") String token) {
        System.out.println(MessageFormat.format("GET /task/all/{0}", id));
        return taskService.getPage(id, 6, token)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }


    @PatchMapping("/{id}")
    public ResponseEntity<?> patchOneTask(@PathVariable UUID id, @RequestHeader("Authorization") String token, @Valid @RequestBody @NotNull Task updatedTask) {
        System.out.println(MessageFormat.format("PATCH /task/{0}", id));
        System.out.println(MessageFormat.format("Body: {0}", updatedTask));

        if (updatedTask.validationFails()) {
            System.out.println("Validation fails");
            return ResponseEntity.badRequest().build();
        }
        return switch (taskService.tryToUpdate(id, updatedTask, token)) {
            case OK -> {
                var task = taskService.getById(id);
                yield task.isPresent()
                        ? ResponseEntity.ok(task.get())
                        : ResponseEntity.notFound().build();
            }
            case NOT_FOUND -> ResponseEntity.notFound().build();
            case UNAUTHORIZED -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("That is not yours to edit!");
            default -> ResponseEntity.badRequest().build();
        };
    }


    @PostMapping
    public ResponseEntity<String> postOneTask(@Valid @RequestBody @NotNull Task newTask, @RequestHeader("Authorization") String token) {
        System.out.println("POST /task");
        System.out.println(MessageFormat.format("Body: {0}", newTask));
        if (newTask.validationFails()) {
            System.out.println("validation failed");
            return ResponseEntity.badRequest().build();
        }
        Task savedTask = taskService.save(newTask, token);
        if (savedTask == null) {
            return ResponseEntity.status(NETWORK_AUTHENTICATION_REQUIRED)
                    .body("Session expired: Log in again and retry");
        }
        if (savedTask.getId() == null) {
            return ResponseEntity.status(UNAUTHORIZED)
                    .body("You are not allowed to add a new task");
        }
        return ResponseEntity.status(CREATED)
                .body(savedTask.getId().toString());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOneTask(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        System.out.println(MessageFormat.format("DELETE /task/{0}", id));
        return switch (taskService.tryToDelete(id, token)) {
            case NO_CONTENT -> ResponseEntity.noContent().build();
            case NOT_FOUND -> ResponseEntity.notFound().build();
            case NETWORK_AUTHENTICATION_REQUIRED -> ResponseEntity.status(NETWORK_AUTHENTICATION_REQUIRED)
                    .body("Session expired: Log in again and retry");
            case UNAUTHORIZED -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("That is not yours to delete!");
            default -> ResponseEntity.badRequest().build();
        };
    }

    @DeleteMapping("/batch")
    public ResponseEntity<String> deleteTasksBatch
            (@RequestBody @NotNull List<UUID> ids, @RequestHeader("Authorization") String token) {
        System.out.println("DELETE /task/batch");
        System.out.println(MessageFormat.format("Body: [{0}]", ids.stream().map(UUID::toString).reduce("", (s1, s2) -> s1 + ", " + s2)));
        return switch (taskService.batchDelete(ids, token)) {
            case NO_CONTENT -> ResponseEntity.noContent().build();
            case NOT_FOUND -> ResponseEntity.notFound().build();

            case UNAUTHORIZED -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("You tried deleting some tasks that do not belong to you!");
            default -> ResponseEntity.badRequest().build();
        };
    }
}

