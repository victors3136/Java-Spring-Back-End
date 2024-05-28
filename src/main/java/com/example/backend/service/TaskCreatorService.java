package com.example.backend.service;

import com.example.backend.model.Task;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;

@Service
@CrossOrigin
public class TaskCreatorService {
    private final TaskService taskService;

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

    public TaskCreatorService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void createEntity() {
//        Task newTask = new Task(
//                activities[(int) (generator % activities.length)] + " @ " + subjects[(int) (generator % subjects.length)],
//                "None provided",
//                (byte) (generator % 10 + 1),
//                Instant.now().plus((((generator++) % 10) + (random.nextInt() % 10) - 5), ChronoUnit.DAYS),
//                UUID.randomUUID());
//         taskService.save(newTask);
    }
}
