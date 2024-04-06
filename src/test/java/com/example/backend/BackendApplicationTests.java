package com.example.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class BackendApplicationTests {

    @Test
    void contextLoads() {
    }

}

@SpringBootTest
class SmokeTest {

    @Autowired
    private EntryController controller;

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
    }
}

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class HttpRequestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void greetingShouldReturnDefaultMessage() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/pagina-principala",
                String.class)).contains("Hello world!");
    }
}

@WebMvcTest(EntryController.class)
class EntryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateEntry() throws Exception {
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
    public void testUpdateEntry() throws Exception {
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
}