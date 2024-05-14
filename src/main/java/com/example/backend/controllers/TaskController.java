package com.example.backend.controllers;

import com.example.backend.model.Task;
import com.example.backend.repository.TaskRepository;
import com.example.backend.service.JSONWebTokenGeneratorService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    private final JSONWebTokenGeneratorService jsonWebTokenGeneratorService;

    private UUID originalUserID(String token) {
        return jsonWebTokenGeneratorService.decode(token.replace("Bearer ", ""));
    }

    private boolean taskBelongsToTokenHolder(Task task, String token) {
        return task.getUser().equals(originalUserID(token));
    }

    @Autowired
    public TaskController(TaskRepository taskRepository, JSONWebTokenGeneratorService jsonWebTokenGeneratorService) {
        this.taskRepository = taskRepository;
        this.jsonWebTokenGeneratorService = jsonWebTokenGeneratorService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOneTask(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        System.out.println(MessageFormat.format("GET /task/{0}", id));

        Optional<Task> maybeTask = taskRepository.findById(id);
        if (maybeTask.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(MessageFormat.format("Invalid ID -- {0}", id));
        }
        Task task = maybeTask.get();

        if (!taskBelongsToTokenHolder(task, token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("That is not yours to see!");
        }

        return ResponseEntity.ok(task);
    }

    @GetMapping("/count/{id}")
    public ResponseEntity<?> getSubtaskCount(@PathVariable UUID id) {
        System.out.println(MessageFormat.format("GET /task/count/{0}", id));
        return ResponseEntity.ok(taskRepository.countSubtasksByTask(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Task>> getAllTasks(@RequestHeader("Authorization") String token) {
        System.out.println("GET /task/all");

        UUID userId = originalUserID(token);

        List<Task> tasks = taskRepository.findByUserId(userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/all/{id}")
    public ResponseEntity<Page<Task>> getTaskPage(@PathVariable int id, @RequestHeader("Authorization") String token) {
        System.out.println(MessageFormat.format("GET /task/all/{0}", id));

        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }

        UUID userId = originalUserID(token);

        Specification<Task> whereClause = (task, query, where) ->
                where.equal(task.get("user").get("id"), userId);

        Pageable pageable = PageRequest.of(id, 6);
        Page<Task> taskPage = taskRepository.findAll(whereClause, pageable);

        return ResponseEntity.ok(taskPage);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<?> patchOneTask(@PathVariable UUID id, @RequestHeader("Authorization") String token, @Valid @RequestBody @NotNull Task updatedTask) {
        System.out.println(MessageFormat.format("PATCH /task/{0}", id));
        System.out.println(MessageFormat.format("Body: {0}", updatedTask));

        if (updatedTask.validationFails()) {
            return ResponseEntity.badRequest().build();
        }


        Optional<Task> maybeTask = taskRepository.findById(id);
        if (maybeTask.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Task task = maybeTask.get();
        if (!taskBelongsToTokenHolder(task, token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("That is not yours to edit!");
        }

        task.setName(updatedTask.getName());
        task.setDescription(updatedTask.getDescription());
        task.setPriority(updatedTask.getPriority());
        task.setDueDate(updatedTask.getDueDate());

        Task savedTask = taskRepository.save(task);

        return ResponseEntity.ok(savedTask);
    }


    @PostMapping("")
    public ResponseEntity<UUID> postOneTask(@Valid @RequestBody @NotNull Task newTask, @RequestHeader("Authorization") String token) {
        System.out.println("POST /task");
        System.out.println(MessageFormat.format("Body: {0}", newTask));
        if (newTask.validationFails()) {
            return ResponseEntity.badRequest().build();
        }

        UUID userId = originalUserID(token);
        newTask.setUser(userId);

        Task savedTask = taskRepository.save(newTask);
        return new ResponseEntity<>(savedTask.getId(), HttpStatus.CREATED);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOneTask(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        System.out.println(MessageFormat.format("DELETE /task/{0}", id));

        Optional<Task> existingTaskOptional = taskRepository.findById(id);
        if (existingTaskOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Task existingTask = existingTaskOptional.get();
        if (!taskBelongsToTokenHolder(existingTask, token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("That is not yours to delete!");
        }
        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<?> deleteTasksBatch(@RequestBody @NotNull List<UUID> ids, @RequestHeader("Authorization") String token) {
        System.out.println("DELETE /task/batch");
        System.out.println(MessageFormat.format("Body: [{0}]", ids.stream().map(UUID::toString).reduce("", (s1, s2) -> s1 + ", " + s2)));

        List<Task> tasksToDelete = taskRepository
                .findAllById(ids)
                .stream()
                .filter(task -> taskBelongsToTokenHolder(task, token))
                .toList();

        if (!tasksToDelete.isEmpty()) {
            taskRepository.deleteAll(tasksToDelete);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    public Task addTask(Task newTask) {
        return taskRepository.save(newTask);
    }

    @SuppressWarnings("unused")
    public TaskRepository getTasksRepository() {
        return taskRepository;
    }
}

