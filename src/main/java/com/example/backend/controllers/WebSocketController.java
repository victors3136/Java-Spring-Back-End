package com.example.backend.controllers;

import com.example.backend.model.Task;
import com.example.backend.service.SubtaskCreatorService;
import com.example.backend.service.TaskCreatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@SuppressWarnings("unused")
@Controller
@CrossOrigin(origins = "*")
public class WebSocketController {

    private final SimpMessagingTemplate template;

    private final TaskCreatorService taskCreatorService;

    private final SubtaskCreatorService subtaskCreatorService;

    @Autowired
    public WebSocketController(SimpMessagingTemplate template, TaskCreatorService taskCreatorService, SubtaskCreatorService subtaskCreatorService) {
        this.template = template;
        this.taskCreatorService = taskCreatorService;
        this.subtaskCreatorService = subtaskCreatorService;
    }

    //    @Scheduled(fixedDelay = 30_000, initialDelay = 10_000)
    public void sendNewEntry() {
        Task newTask = taskCreatorService.createEntity();

        if (newTask != null)
            template.convertAndSend("/topic/newEntry", newTask);
    }

    //    @Scheduled(fixedDelay = 25_000, initialDelay = 20_000)
    public void createSubentry() {
        subtaskCreatorService.createEntity();
    }
}
