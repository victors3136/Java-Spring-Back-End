package com.example.backend.service;

import com.example.backend.model.Task;
import com.example.backend.repository.TaskRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@CrossOrigin
public class TaskCreatorService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private long generator = 0;
    private static final String[] subjects = {
            "Web Programming",
            "Software Engineering",
            "Artificial Intelligence",
            "Systems for Design and Implementation",
            "Database Management Systems",
            "MSG",
            "Licenta"};
    private static final String[] activities = {
            "Homework",
            "Project",
            "Lecture",
            "Lab"
    };
    private static final Random random = new Random(Instant.now().toEpochMilli());

    private List<UUID> usersWithAddPermission() {
        return userRepository.findByPermission("add");
    }

    public TaskCreatorService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Task createEntity() {
        List<UUID> availableUsers = usersWithAddPermission();
        if (availableUsers.isEmpty()) {
            return null;
        }
        return taskRepository.save(
                new Task(
                        "%s @ %s".formatted(
                                activities[(int) (generator % activities.length)],
                                subjects[(int) (generator % subjects.length)]),
                        "None provided",
                        (byte) (generator % 10 + 1),
                        Instant.now().plus(
                                (((generator++) % 10) + (random.nextInt() % 10) - 5),
                                ChronoUnit.DAYS
                        ),
                        availableUsers.get(random.nextInt() % availableUsers.size())
                )
        );
    }
}
