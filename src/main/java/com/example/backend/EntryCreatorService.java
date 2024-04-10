package com.example.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.Instant;

@Service
@CrossOrigin
public class EntryCreatorService {
    @Autowired
    private EntryController entryController;

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

    public Entry createEntity() {
        Entry newEntry = new Entry(
                activities[(int) (generator % activities.length)] + " @ " + subjects[(int) (generator % subjects.length)],
                "None provided",
                (byte) ((generator++) % 10 + 1),
                Instant.now());
        if (entryController.addEntry(newEntry))
            return newEntry;
        return null;
    }
}
