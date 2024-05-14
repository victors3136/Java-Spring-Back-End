package modules;

import com.example.backend.controllers.TaskController;
import com.example.backend.model.Task;
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

@WebMvcTest(TaskController.class)
public class TaskTaskControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void postTask_PostingCorrectTask_ReturnsIsCreated() {
        Task correctTask = new Task("e", "", (byte) 5, Instant.now(), UUID.randomUUID());
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
        Task correctTask = new Task("e", "", (byte) 5, Instant.now(), UUID.randomUUID());
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
        Task taskLackingName = new Task("", "", (byte) 5, Instant.now(), UUID.randomUUID());
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
        Task taskWithNullDesc = new Task("", null, (byte) 5, Instant.now(), UUID.randomUUID());
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
        Task taskWithWrongPriority = new Task("e", "", (byte) -1, Instant.now(), UUID.randomUUID());
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
        Task taskWithNullDueDate = new Task("", "", (byte) 5, null, UUID.randomUUID());
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
        Task originalTask = new Task("Old Name", "Old Description", (byte) 7, Instant.now(), UUID.randomUUID());
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
        Task updatedTask = new Task("New Name", "New Description", (byte) 9, Instant.now(), UUID.randomUUID());

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
        Task originalTask = new Task("Old Name", "Old Description", (byte) 7, Instant.now(), UUID.randomUUID());
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
        Task badNameTask = new Task("", "New Description", (byte) 9, Instant.now(), UUID.randomUUID());
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
        Task originalTask = new Task("Old Name", "Old Description", (byte) 7, Instant.now(), UUID.randomUUID());
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
        Task badPriorityTask = new Task("yaaa", "New Description", (byte) 19, Instant.now(), UUID.randomUUID());
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
        Task task = new Task("Old Name", "Old Description", (byte) 7, Instant.now(), UUID.randomUUID());
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
        Task task = new Task("aaa", "new Desc", (byte) 1, Instant.now(), UUID.randomUUID());
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
        Task t1 = new Task("A", "A", (byte) 1, Instant.now(), UUID.randomUUID()),
                t2 = new Task("B", "B", (byte) 2, Instant.now(), UUID.randomUUID());
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
        Task task = new Task("A", "A", (byte) 1, Instant.now(), UUID.randomUUID());
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
        Task task = new Task("A", "A", (byte) 1, Instant.now(), UUID.randomUUID());
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
        Task task = new Task("A", "A", (byte) 1, Instant.now(), UUID.randomUUID());
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
        Task task = new Task("A", "A", (byte) 1, Instant.now(), UUID.randomUUID());
        try {
            mockMvc.perform(delete("/task/{id}", task.getId()))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            fail();
        }
    }
}