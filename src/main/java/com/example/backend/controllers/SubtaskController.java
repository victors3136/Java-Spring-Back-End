package com.example.backend.controllers;

import com.example.backend.exceptions.InvalidJWTException;
import com.example.backend.exceptions.NotFoundException;
import com.example.backend.exceptions.PermissionDeniedException;
import com.example.backend.exceptions.ValidationException;
import com.example.backend.model.Subtask;
import com.example.backend.service.SubtaskService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

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

    @GetMapping("/count/{id}")
    public ResponseEntity<?> getSubtaskCount(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        System.out.println(MessageFormat.format("GET /subtask/count/{0}", id));
        try {
            return ResponseEntity.ok(subtaskService.countSubtasksByTask(id, token));
        } catch (InvalidJWTException ignored) {
            return ResponseEntity.status(NETWORK_AUTHENTICATION_REQUIRED).body("Session expired");
        } catch (PermissionDeniedException ignored) {
            return ResponseEntity.status(UNAUTHORIZED).body("Permission denied");
        }

    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchOneSubtask(@PathVariable UUID id, @Valid @RequestBody @NotNull Subtask updatedSubtask, @RequestHeader("Authorization") String token) {
        System.out.println(MessageFormat.format("PATCH /subtask/{0}", id));
        System.out.println(MessageFormat.format("Body: {0}", updatedSubtask));
        try {
            return ResponseEntity.ok(subtaskService.tryToUpdate(id, updatedSubtask, token));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        } catch (InvalidJWTException e) {
            return ResponseEntity.status(NETWORK_AUTHENTICATION_REQUIRED).body("Session expired");
        } catch (PermissionDeniedException e) {
            return ResponseEntity.status(UNAUTHORIZED).body("Permission Denied");
        } catch (NotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body("Not Found");
        }
    }

    @PostMapping
    public ResponseEntity<UUID> postOneSubtask(@Valid @RequestBody @NotNull Subtask newSubtask, @RequestHeader("Authorization") String token) {
        System.out.println("POST /subtask");
        System.out.println(MessageFormat.format("Body: {0}", newSubtask));
        try {
            return ResponseEntity.status(CREATED).body(subtaskService.save(newSubtask, token).getId());
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOneSubtask(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        System.out.println(MessageFormat.format("DELETE /subtask/{0}", id));
        try {
            subtaskService.tryToDelete(id, token);
            return ResponseEntity.noContent().build();
        } catch (InvalidJWTException e) {
            return ResponseEntity.status(NETWORK_AUTHENTICATION_REQUIRED).body("Session expired");
        } catch (PermissionDeniedException e) {
            return ResponseEntity.status(UNAUTHORIZED).body("Permission Denied");
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/for/{id}")
    public ResponseEntity<List<Subtask>> getSubtasksByParentId(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        System.out.println(MessageFormat.format("GET /subtask/by_parent/{0}", id));
        return ResponseEntity.ok(subtaskService.getForTask(id, token).stream().toList());
    }
}
