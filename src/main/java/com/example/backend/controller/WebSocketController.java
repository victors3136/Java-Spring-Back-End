package com.example.backend.controller;

import com.example.backend.model.Task;
import com.example.backend.service.SubtaskCreatorService;
import com.example.backend.service.TaskCreatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@CrossOrigin(origins = "*")
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private TaskCreatorService taskCreatorService;

    @Autowired
    private SubtaskCreatorService subtaskCreatorService;

    @Scheduled(fixedDelay = 100, initialDelay = 5_000)
    public void sendNewEntry() {
        Task newTask = taskCreatorService.createEntity();

        if (newTask != null)
            template.convertAndSend("/topic/newEntry", newTask);
    }

    @Scheduled(fixedDelay = 500, initialDelay = 10_000)
    public void createSubentry() {
        subtaskCreatorService.createEntity();
    }
}
