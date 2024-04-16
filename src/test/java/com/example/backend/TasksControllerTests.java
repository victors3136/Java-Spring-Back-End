package com.example.backend;

import com.example.backend.controller.Controller;
import com.example.backend.model.Tasks;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(Controller.class)
public class TasksControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void postTask_PostingCorrectTask_ReturnsIsCreated() {
        Tasks correctTask = new Tasks("e", "", (byte) 5, Instant.now());
        try {
            var x = post("/task")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(correctTask.toString());
            mockMvc.perform(x)
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            System.out.println(e.getClass().getName());
            fail();
        }
    }

    @Test
    public void postTask_PostingSameTaskTwice_ReturnsIsCreated() {
        Tasks correctTask = new Tasks("e", "", (byte) 5, Instant.now());
        try {
            mockMvc.perform(post("/task")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(correctTask.toString()))
                    .andExpect(status().isCreated());
            mockMvc.perform(post("/task")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(correctTask.toString()))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void postTask_PostingTaskWithNoName_ReturnsBadRequest() {
        Tasks taskLackingName = new Tasks("", "", (byte) 5, Instant.now());
        try {
            mockMvc.perform(post("/task")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(taskLackingName.toString()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            fail();
        }
    }


    @Test
    public void postTask_PostingTaskWithNullDescription_ReturnsBadRequest() {
        Tasks taskWithNullDesc = new Tasks("", null, (byte) 5, Instant.now());
        try {
            mockMvc.perform(post("/task")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(taskWithNullDesc.toString()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void postTask_PostingTaskWithOutOfRangePriority_ReturnsBadRequest() {
        Tasks taskWithWrongPriority = new Tasks("e", "", (byte) -1, Instant.now());
        try {
            mockMvc.perform(post("/task")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(taskWithWrongPriority.toString()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void postTask_PostingTaskWithNullDueDate_ReturnsBadRequest() {
        Tasks taskWithNullDueDate = new Tasks("", "", (byte) 5, null);
        try {
            mockMvc.perform(post("/task")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(taskWithNullDueDate.toString()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void patchTask_PatchingWithValidTask_ReturnsOk() {
        Tasks originalTask = new Tasks("Old Name", "Old Description", (byte) 7, Instant.now());
        /// Update the id of the task to the value returned by the server
        try {
            originalTask.setId(UUID.fromString(
                    mockMvc.perform(post("/task")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(originalTask.toString()))
                            .andReturn()
                            .getResponse()
                            .getContentAsString()
                            .replaceAll("\"", "")
            ));
        } catch (Exception e) {
            fail();
        }
        Tasks updatedTask = new Tasks("New Name", "New Description", (byte) 9, Instant.now());

        try {
            mockMvc.perform(patch("/task/{id}", originalTask.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updatedTask.toString()))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void patchTask_PatchingWithInvalidTaskName_ReturnsBadRequest() {
        Tasks originalTask = new Tasks("Old Name", "Old Description", (byte) 7, Instant.now());
        /// Update the id of the task to the value returned by the server
        try {
            originalTask.setId(UUID.fromString(
                    mockMvc.perform(post("/task")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(originalTask.toString()))
                            .andReturn()
                            .getResponse()
                            .getContentAsString()
                            .replaceAll("\"", "")
            ));
        } catch (Exception e) {
            fail();
        }
        Tasks badNameTask = new Tasks("", "New Description", (byte) 9, Instant.now());
        try {
            mockMvc.perform(patch("/task/{id}", originalTask.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(badNameTask.toString()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void patchTask_PatchingWithInvalidTaskPriority_ReturnsBadRequest() {
        Tasks originalTask = new Tasks("Old Name", "Old Description", (byte) 7, Instant.now());
        /// Update the id of the task to the value returned by the server
        try {
            originalTask.setId(UUID.fromString(
                    mockMvc.perform(post("/task")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(originalTask.toString()))
                            .andReturn()
                            .getResponse()
                            .getContentAsString()
                            .replaceAll("\"", "")
            ));
        } catch (Exception e) {
            fail();
        }
        Tasks badPriorityTask = new Tasks("yaaa", "New Description", (byte) 19, Instant.now());
        try {
            mockMvc.perform(patch("/task/{id}", originalTask.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(badPriorityTask.toString()))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void getTask_TaskExists_ReturnsOkAndTaskAsJSON() {
        Tasks task = new Tasks("Old Name", "Old Description", (byte) 7, Instant.now());
        try {
            task.setId(UUID.fromString(
                    mockMvc.perform(post("/task")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(task.toString()))
                            .andReturn()
                            .getResponse()
                            .getContentAsString()
                            .replaceAll("\"", "")));
        } catch (Exception e) {
            fail();
        }
        try {
            mockMvc.perform(get("/task/{id}", task.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(task.getName()))
                    .andExpect(jsonPath("$.description").value(task.getDescription()));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void getTask_TaskDoesNotExist_ReturnsNotFound() {
        Tasks task = new Tasks("aaa", "new Desc", (byte) 1, Instant.now());
        try {
            mockMvc.perform(get("/task/{id}", task.getId()))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void getAll_EmptyStorage_ReturnsJSONArrayOfSize0() {
        try {
            mockMvc.perform(get("/task/all"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(0)));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void getAll_StorageWith2Items_ReturnsJSONArrayOfSize2WithCorrectElementsInAnyOrder() {
        ///Setup
        Tasks t1 = new Tasks("A", "A", (byte) 1, Instant.now()),
                t2 = new Tasks("B", "B", (byte) 2, Instant.now());
        try {
            mockMvc.perform(post("/task")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(t1.toString()));
        } catch (Exception e) {
            fail();
        }
        try {
            mockMvc.perform(post("/task")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(t2.toString()));
        } catch (Exception e) {
            fail();
        }
        /// what we actually test
        try {
            mockMvc.perform(get("/task/all"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[*].name", containsInAnyOrder("A", "B")))
                    .andExpect(jsonPath("$[*].description", containsInAnyOrder("A", "B")));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void deleteEntry_DeleteATaskPresentInStorage_ReturnsNoContent() {
        Tasks task = new Tasks("A", "A", (byte) 1, Instant.now());
        try {
            task.setId(UUID.fromString(
                    mockMvc.perform(post("/task")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(task.toString()))
                            .andReturn()
                            .getResponse()
                            .getContentAsString()
                            .replaceAll("\"", "")));
        } catch (Exception e) {
            fail();
        }

        try {
            mockMvc.perform(delete("/task/{id}", task.getId()))
                    .andExpect(status().isNoContent());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void deleteEntry_DeleteATaskPresentInStorageTwice_ReturnsNotFound() {
        Tasks task = new Tasks("A", "A", (byte) 1, Instant.now());
        try {
            task.setId(UUID.fromString(
                    mockMvc.perform(post("/task")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(task.toString()))
                            .andReturn()
                            .getResponse()
                            .getContentAsString()
                            .replaceAll("\"", "")));
        } catch (Exception e) {
            fail();
        }

        try {
            mockMvc.perform(delete("/task/{id}", task.getId()));
        } catch (Exception e) {
            fail();
        }
        try {
            mockMvc.perform(delete("/task/{id}", task.getId()))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void deleteEntry_DeleteATaskPresentInStorage_DecreasesNumberOfTasks() {
        Tasks task = new Tasks("A", "A", (byte) 1, Instant.now());
        try {
            task.setId(UUID.fromString(
                    mockMvc.perform(post("/task")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(task.toString()))
                            .andReturn()
                            .getResponse()
                            .getContentAsString()
                            .replaceAll("\"", "")));
        } catch (Exception e) {
            fail();
        }
        try {
            mockMvc.perform(get("/task/all"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(1)));
        } catch (Exception e) {
            fail();
        }
        try {
            mockMvc.perform(delete("/task/{id}", task.getId()));
        } catch (Exception e) {
            fail();
        }
        try {
            mockMvc.perform(get("/task/all"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(0)));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void deleteEntry_DeleteATaskNotPresentInStorage_ReturnsNotFound() {
        Tasks task = new Tasks("A", "A", (byte) 1, Instant.now());
        try {
            mockMvc.perform(delete("/task/{id}", task.getId()))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            fail();
        }
    }
}