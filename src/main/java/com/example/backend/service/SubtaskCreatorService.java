package com.example.backend.service;

import com.example.backend.model.Subtask;
import com.example.backend.model.Task;
import com.example.backend.repository.SubtaskRepository;
import com.example.backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;


@Service
@CrossOrigin
public class SubtaskCreatorService {
    private final SubtaskRepository subtaskRepo;
    private final TaskRepository taskRepo;
    private long generator = 0;
    private static final String[] subjects = {
            "Stop procrastinating",
            "Read that again",
            "Think about this"};

    @Autowired
    public SubtaskCreatorService(SubtaskRepository subtaskRepository, TaskRepository taskRepository) {
        this.subtaskRepo = subtaskRepository;
        this.taskRepo = taskRepository;
    }

    public Subtask createEntity() {
        List<Task> tasksList = taskRepo.findAll();
        if (tasksList.isEmpty()) {
            return null;
        }
        Subtask newSubtask = new Subtask(
                subjects[(int) (generator % subjects.length)],
                tasksList.get((int) ((generator++) % tasksList.size())).getId()
        );
        return subtaskRepo.save(newSubtask);
    }
}
