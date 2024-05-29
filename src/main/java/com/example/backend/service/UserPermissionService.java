package com.example.backend.service;

import com.example.backend.model.Subtask;
import com.example.backend.model.Task;
import com.example.backend.repository.TaskRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@SuppressWarnings("unused")
@Service
public class UserPermissionService {
    private final Function<UUID, List<String>> getUserPermissions;
    private final Function<UUID, Optional<Task>> tryGetParentTaskForSubtask;

    @Autowired
    UserPermissionService(UserRepository userRepository, TaskRepository taskRepository) {
        getUserPermissions = userRepository::findPermissionsByUserId;
        tryGetParentTaskForSubtask = taskRepository::findById;
    }

    public boolean canCreate(UUID user) {
        return canCreate(getUserPermissions.apply(user));
    }

    public boolean canCreate(List<String> permissions) {
        return permissions.contains("add");
    }

    public boolean canRead(UUID user) {
        return canRead(getUserPermissions.apply(user));
    }

    public boolean canRead(List<String> permissions) {
        return permissions.contains("view");
    }

    public boolean canUpdate(UUID user, Task task) {
        return canUpdate(user, task, getUserPermissions.apply(user));
    }

    public boolean canUpdate(UUID user, Task task, List<String> permissions) {
        if (permissions.contains("edit-any")) return true;
        if (permissions.contains("edit-own")) return task.getUser().equals(user);
        return false;
    }

    public boolean canUpdate(UUID user, Subtask subtask) {
        return canUpdate(user, subtask, getUserPermissions.apply(user));
    }

    public boolean canUpdate(UUID user, Subtask subtask, List<String> permissions) {
        System.out.println(permissions.stream().reduce("", (s1, s2) -> s1 + " " + s2));
        if (permissions.contains("edit-any")) return true;
        if (permissions.contains("edit-own")) {
            return tryGetParentTaskForSubtask
                    .apply(subtask.getTask())
                    .map(task -> task.getUser().equals(user))
                    .orElse(false);
        }
        return false;
    }

    public boolean canDelete(Task task, UUID user) {
        return canDelete(task, user, getUserPermissions.apply(user));
    }

    public boolean canDelete(Subtask subtask, UUID user) {
        return canDelete(subtask, user, getUserPermissions.apply(user));
    }

    public boolean canDelete(Task task, UUID user, List<String> permissions) {
        if (permissions.contains("delete-any")) return true;
        if (permissions.contains("delete-own")) return task.getUser().equals(user);
        return false;
    }

    public boolean canDelete(Subtask subtask, UUID user, List<String> permissions) {
        if (permissions.contains("delete-any")) return true;
        if (permissions.contains("delete-own")) {
            return tryGetParentTaskForSubtask
                    .apply(subtask.getTask())
                    .map(task -> task.getUser().equals(user))
                    .orElse(false);
        }
        return false;
    }

    public boolean canDeleteBatch(UUID user) {
        return canDeleteBatch(getUserPermissions.apply(user));
    }

    public boolean canDeleteBatch(List<String> permissions) {
        return permissions.contains("delete-batch");
    }

    public boolean canAssign(UUID user) {
        return canAssign(getUserPermissions.apply(user));
    }

    public boolean canAssign(List<String> permissions) {
        return permissions.contains("assign");
    }

    public boolean canIKickIt(UUID user) {
        return canIKickIt(getUserPermissions.apply(user));
    }

    public boolean canIKickIt(List<String> permissions) {
        return permissions.contains("kick");
    }
}
