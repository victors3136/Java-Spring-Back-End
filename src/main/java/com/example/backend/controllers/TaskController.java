package com.example.backend.controllers;

import com.example.backend.exceptions.ApplicationException;
import com.example.backend.model.Task;
import com.example.backend.service.TaskService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

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
        try {
            return ResponseEntity.ok(taskService.getById(id, token));
        } catch (ApplicationException e) {
            return e.asHttpResponse();
        }
    }


    @GetMapping("/all")
    public ResponseEntity<?> getAllTasks(@RequestHeader("Authorization") String token) {
        System.out.println("GET /task/all");
        try {
            return ResponseEntity.ok(taskService.getAll(token).stream().toList());
        } catch (ApplicationException e) {
            System.out.println("Error at page request " + e.message());
            return e.asHttpResponse();
        }
    }

    @GetMapping("/all/{id}")
    public ResponseEntity<?> getTaskPage(@PathVariable int id, @RequestHeader("Authorization") String token) {
        System.out.println(MessageFormat.format("GET /task/all/{0}", id));
        try {

            return ResponseEntity.ok(taskService.getPage(id, 6, token));
        } catch (ApplicationException e) {
            System.out.println("Error at page request");
            return e.asHttpResponse();
        }
    }


    @PatchMapping("/{id}")
    public ResponseEntity<?> patchOneTask(@PathVariable UUID id, @RequestHeader("Authorization") String token, @Valid @RequestBody @NotNull Task updatedTask) {
        System.out.println(MessageFormat.format("PATCH /task/{0}", id));
        System.out.println(MessageFormat.format("Body: {0}", updatedTask));
        try {
            return ResponseEntity.ok(taskService.tryToUpdate(id, updatedTask, token));
        } catch (ApplicationException e) {
            return e.asHttpResponse();
        }
    }


    @PostMapping
    public ResponseEntity<?> postOneTask(@Valid @RequestBody @NotNull Task newTask, @RequestHeader("Authorization") String token) {
        System.out.println("POST /task");
        System.out.println(MessageFormat.format("Body: {0}", newTask));
        try {
            return ResponseEntity.status(CREATED).body(taskService.save(newTask, token).getId().toString());
        } catch (ApplicationException e) {
            return e.asHttpResponse();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOneTask(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        System.out.println(MessageFormat.format("DELETE /task/{0}", id));
        try {
            taskService.tryToDelete(id, token);
        } catch (ApplicationException e) {
            return e.asHttpResponse();
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<?> deleteTasksBatch
            (@RequestBody @NotNull List<UUID> ids, @RequestHeader("Authorization") String token) {
        System.out.println("DELETE /task/batch");
        System.out.println(MessageFormat.format("Body: [{0}]", ids.stream().map(UUID::toString).reduce("", (s1, s2) -> s1 + ", " + s2)));
        try {
            taskService.batchDelete(ids, token);
        } catch (ApplicationException e) {
            return e.asHttpResponse();
        }
        return ResponseEntity.noContent().build();
    }
}

