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
        if (!taskService.tryToUpdate(id, updatedTask, token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("That is not yours to edit!");
        }
        var task = taskService.getById(id);
        return task.isPresent()
                ? ResponseEntity.ok(task.get())
                : ResponseEntity.notFound().build();
    }


    @PostMapping
    public ResponseEntity<UUID> postOneTask(@Valid @RequestBody @NotNull Task newTask, @RequestHeader("Authorization") String token) {
        System.out.println("POST /task");
        System.out.println(MessageFormat.format("Body: {0}", newTask));
        if (newTask.validationFails()) {
            System.out.println("validation failed");
            return ResponseEntity.badRequest().build();
        }
        Task savedTask = taskService.save(newTask, token);
        return new ResponseEntity<>(savedTask.getId(), HttpStatus.CREATED);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOneTask(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        System.out.println(MessageFormat.format("DELETE /task/{0}", id));
        return taskService.tryToDelete(id, token)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("That is not yours to delete!");
    }

    @DeleteMapping("/batch")
    public ResponseEntity<?> deleteTasksBatch(@RequestBody @NotNull List<UUID> ids, @RequestHeader("Authorization") String token) {
        System.out.println("DELETE /task/batch");
        System.out.println(MessageFormat.format("Body: [{0}]", ids.stream().map(UUID::toString).reduce("", (s1, s2) -> s1 + ", " + s2)));
        return taskService.batchDelete(ids, token)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}

