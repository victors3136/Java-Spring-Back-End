package com.example.backend.controller;

import com.example.backend.service.EntryCreatorService;
import com.example.backend.model.Task;
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
    private EntryCreatorService entryCreatorService;

    @Scheduled(fixedDelay = 10000, initialDelay = 5000)
    public void sendNewEntry() {
        Task newTask = entryCreatorService.createEntity();
//        System.out.println(newEntry);
        if (newTask != null)
            template.convertAndSend("/topic/newEntry", newTask);
    }
}
