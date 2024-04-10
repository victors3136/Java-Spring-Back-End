package com.example.backend;

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
        Entry newEntry = entryCreatorService.createEntity();
        System.out.println(newEntry);
        if (newEntry != null)
            template.convertAndSend("/topic/newEntry", newEntry);
    }
}
