package com.example.backend.service;

import com.example.backend.model.Subtask;
import com.example.backend.model.Task;
import com.example.backend.utils.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;


@Service
@CrossOrigin
public class SubtaskCreatorService {
    private final SubtaskService subtaskService;
    private final TaskService taskService;
    private long generator = 0;
    private static final String[] subjects = {
            "Stop procrastinating",
            "Read that again",
            "Think about this"};

    @Autowired
    public SubtaskCreatorService(SubtaskService subtaskService, TaskService taskService) {
        this.subtaskService = subtaskService;
        this.taskService = taskService;
    }

    public void createEntity() {
//        List<Task> tasksList = taskService.getAll().stream().toList();
//        if (tasksList.isEmpty()) {
//            return;
//        }
//        Subtask newSubtask = new Subtask(
//                subjects[(int) (generator % subjects.length)],
//                tasksList.get((int) ((generator++) % tasksList.size())).getId()
//        );
//        subtaskService.save(newSubtask);
    }
}
