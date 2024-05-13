package com.example.backend.controller;

import com.example.backend.model.Subtask;
import com.example.backend.model.Task;
import com.example.backend.service.SubtaskCreatorService;
import com.example.backend.service.TaskCreatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Deprecated
@Controller
@CrossOrigin(origins = "*")
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private TaskCreatorService taskCreatorService;

    @Autowired
    private SubtaskCreatorService subtaskCreatorService;

//    @Scheduled(fixedDelay = 60_000, initialDelay = 1_000)
    public void sendNewEntry() {
        Task newTask = taskCreatorService.createEntity();

        System.out.println("GENERATE /task");
        System.out.println(newTask);

        if (newTask != null) {
            template.convertAndSend("/topic/updates", newTask);
        }
    }

//    @Scheduled(fixedDelay = 60_000, initialDelay = 2_000)
    public void createSubentry() {
        Subtask newSubtask = subtaskCreatorService.createEntity();

        System.out.println("GENERATE /subtask");
        System.out.println(newSubtask);
    }
}
