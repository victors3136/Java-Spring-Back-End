package com.example.backend.service;

import com.example.backend.controller.Controller;
import com.example.backend.model.Subtask;
import com.example.backend.model.Tasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;


@Service
@CrossOrigin
public class SubtaskCreatorService {
    @Autowired
    private Controller controller;
    private long generator = 0;
    private static final String[] subjects = {
            "Stop procrastinating",
            "Read that again",
            "Think about this"};

    public Subtask createEntity() {
        List<Tasks> tasksList = controller.getTasksRepository().findAll();
        if (tasksList.isEmpty()) {
            return null;
        }
        Subtask newTask = new Subtask(
                subjects[(int) (generator % subjects.length)],
                tasksList.get((int) ((generator++) % tasksList.size())).getId()
        );
        controller.addSubtask(newTask);
        return newTask;
    }
}
