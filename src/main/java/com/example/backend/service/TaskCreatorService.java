package com.example.backend.service;

import com.example.backend.controller.TaskController;
import com.example.backend.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;

@Deprecated
@Service
@CrossOrigin
public class TaskCreatorService {
    @Autowired
    private TaskController taskController;

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

    public Task createEntity() {
        Task newTask = new Task(
                activities[(int) (generator % activities.length)] + " @ " + subjects[(int) (generator % subjects.length)],
                "None provided",
                (byte) (generator % 10 + 1),
                Instant.now().plus((((generator++) % 10) + (random.nextInt() % 10) - 5), ChronoUnit.DAYS),
                UUID.randomUUID());
        return taskController.addTask(newTask);
    }
}
