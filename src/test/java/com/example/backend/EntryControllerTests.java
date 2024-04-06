package com.example.backend;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EntryController.class)
public class EntryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new EntryController()).build();
    }

    @AfterEach
    public void tearDown() {
        mockMvc = null;
    }

    @Test
    public void testPostEntry() throws Exception {
        Entry correctEntry = new Entry("e", "", (byte) 5, Instant.now());
        mockMvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(correctEntry.toString()))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(correctEntry.toString()))
                .andExpect(status().isCreated());
        Entry entryLackingName = new Entry("", "", (byte) 5, Instant.now());
        mockMvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entryLackingName.toString()))
                .andExpect(status().isBadRequest());
        Entry entryWithNullDesc = new Entry("", null, (byte) 5, Instant.now());
        mockMvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entryWithNullDesc.toString()))
                .andExpect(status().isBadRequest());
        Entry entryWithWrongPriority = new Entry("e", "", (byte) -1, Instant.now());
        mockMvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entryWithWrongPriority.toString()))
                .andExpect(status().isBadRequest());
        Entry entryWithNullDueDate = new Entry("", "", (byte) 5, null);
        mockMvc.perform(post("/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entryWithNullDueDate.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPatchEntry() throws Exception {
        Entry originalEntry = new Entry("Old Name", "Old Description", (byte) 7, Instant.now());
        mockMvc.perform(post("/entry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(originalEntry.toString()));
        Entry updatedEntry = new Entry("New Name", "New Description", (byte) 9, Instant.now());
        mockMvc.perform(patch("/entry/{id}", originalEntry.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedEntry.toString()))
                .andExpect(status().isOk());
        Entry badNameEntry = new Entry("", "New Description", (byte) 9, Instant.now());
        mockMvc.perform(patch("/entry/{id}", originalEntry.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badNameEntry.toString()))
                .andExpect(status().isBadRequest());
        Entry badPriorityEntry = new Entry("yaaa", "New Description", (byte) 19, Instant.now());
        mockMvc.perform(patch("/entry/{id}", originalEntry.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badPriorityEntry.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetEntry() throws Exception {
        Entry e1 = new Entry("Old Name", "Old Description", (byte) 7, Instant.now()),
                e2 = new Entry("aaa", "new Desc", (byte) 1, Instant.now());
        mockMvc.perform(post("/entry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(e1.toString()));
        mockMvc.perform(get("/entry/{id}", e1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(e1.getName()))
                .andExpect(jsonPath("$.description").value(e1.getDescription()));

        mockMvc.perform(get("/entry/{id}", e2.getId()))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/entry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(e2.toString()));
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
        Entry e1 = new Entry("A", "A", (byte) 1, Instant.now()),
                e2 = new Entry("B", "B", (byte) 2, Instant.now());
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
        Entry e1 = new Entry("A", "A", (byte) 1, Instant.now()),
                e2 = new Entry("B", "B", (byte) 2, Instant.now());
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