package com.example.backend;

import com.example.backend.controller.Controller;
import com.example.backend.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(Controller.class)
public class TaskControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new Controller()).build();
    }

    @AfterEach
    public void tearDown() {
        mockMvc = null;
    }

    @Test
    public void testPostEntry() throws Exception {
        Task correctTask = new Task("e", "", (byte) 5, Instant.now());
        mockMvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(correctTask.toString()))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(correctTask.toString()))
                .andExpect(status().isCreated());
        Task taskLackingName = new Task("", "", (byte) 5, Instant.now());
        mockMvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskLackingName.toString()))
                .andExpect(status().isBadRequest());
        Task taskWithNullDesc = new Task("", null, (byte) 5, Instant.now());
        mockMvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskWithNullDesc.toString()))
                .andExpect(status().isBadRequest());
        Task taskWithWrongPriority = new Task("e", "", (byte) -1, Instant.now());
        mockMvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskWithWrongPriority.toString()))
                .andExpect(status().isBadRequest());
        Task taskWithNullDueDate = new Task("", "", (byte) 5, null);
        mockMvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskWithNullDueDate.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPatchEntry() throws Exception {
        Task originalTask = new Task("Old Name", "Old Description", (byte) 7, Instant.now());
        originalTask.setId(UUID.fromString(
                mockMvc.perform(post("/entry")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(originalTask.toString()))
                        .andReturn()
                        .getResponse()
                        .getContentAsString()
                        .replaceAll("\"", "")
        ));
        Task updatedTask = new Task("New Name", "New Description", (byte) 9, Instant.now());

        mockMvc.perform(patch("/entry/{id}", originalTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedTask.toString()))
                .andExpect(status().isOk());
        Task badNameTask = new Task("", "New Description", (byte) 9, Instant.now());
        mockMvc.perform(patch("/entry/{id}", originalTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badNameTask.toString()))
                .andExpect(status().isBadRequest());
        Task badPriorityTask = new Task("yaaa", "New Description", (byte) 19, Instant.now());
        mockMvc.perform(patch("/entry/{id}", originalTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badPriorityTask.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetEntry() throws Exception {
        Task e1 = new Task("Old Name", "Old Description", (byte) 7, Instant.now()),
                e2 = new Task("aaa", "new Desc", (byte) 1, Instant.now());

        e1.setId(UUID.fromString(
                mockMvc.perform(post("/entry")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(e1.toString()))
                        .andReturn()
                        .getResponse()
                        .getContentAsString()
                        .replaceAll("\"", "")));
        mockMvc.perform(get("/entry/{id}", e1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(e1.getName()))
                .andExpect(jsonPath("$.description").value(e1.getDescription()));

        mockMvc.perform(get("/entry/{id}", e2.getId()))
                .andExpect(status().isNotFound());

        e2.setId(UUID.fromString(
                mockMvc.perform(post("/entry")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(e2.toString()))
                        .andReturn()
                        .getResponse()
                        .getContentAsString()
                        .replaceAll("\"", "")));
        mockMvc.perform(get("/entry/{id}", e2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(e2.getName()))
                .andExpect(jsonPath("$.description").value(e2.getDescription()));
    }

    @Test
    public void testGetAll() throws Exception {
        mockMvc.perform(get("/entries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
        Task e1 = new Task("A", "A", (byte) 1, Instant.now()),
                e2 = new Task("B", "B", (byte) 2, Instant.now());
        mockMvc.perform(post("/entry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(e1.toString()));
        mockMvc.perform(post("/entry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(e2.toString()));
        mockMvc.perform(get("/entries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("A"))
                .andExpect(jsonPath("$[1].name").value("B"))
                .andExpect(jsonPath("$[0].description").value("A"))
                .andExpect(jsonPath("$[1].description").value("B"));
    }

    @Test
    public void testDeleteEntry() throws Exception {
        mockMvc.perform(get("/entries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
        Task e1 = new Task("A", "A", (byte) 1, Instant.now()),
                e2 = new Task("B", "B", (byte) 2, Instant.now());
        e1.setId(UUID.fromString(
                mockMvc.perform(post("/entry")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(e1.toString()))
                        .andReturn()
                        .getResponse()
                        .getContentAsString()
                        .replaceAll("\"", "")));
        e2.setId(UUID.fromString(
                mockMvc.perform(post("/entry")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(e2.toString()))
                        .andReturn()
                        .getResponse()
                        .getContentAsString()
                        .replaceAll("\"", "")));
        mockMvc.perform(get("/entries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(delete("/entry/{id}", e1.getId()))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/entry/{id}", e1.getId()))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/entries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(delete("/entry/{id}", e1.getId()))
                .andExpect(status().isNotFound());
    }
}