package com.example.backend.service;

import com.example.backend.controller.TaskController;
import com.example.backend.model.Subtask;
import com.example.backend.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;


@Service
@CrossOrigin
public class SubtaskCreatorService {
    @Autowired
    private TaskController taskController;
    private long generator = 0;
    private static final String[] subjects = {
            "Stop procrastinating",
            "Read that again",
            "Think about this"};

    @SuppressWarnings("unused")
    public Subtask createEntity() {
        List<Task> tasksList = taskController.getTasksRepository().findAll();
        if (tasksList.isEmpty()) {
            return null;
        }
        Subtask newTask = new Subtask(
                subjects[(int) (generator % subjects.length)],
                tasksList.get((int) ((generator++) % tasksList.size())).getId()
        );
        return taskController.addSubtask(newTask);
    }
}
