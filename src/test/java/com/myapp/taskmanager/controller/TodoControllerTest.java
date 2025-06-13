package com.myapp.taskmanager.controller;

import com.myapp.taskmanager.entity.Todo;
import com.myapp.taskmanager.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

//    @Autowired
    @MockitoBean
    private TodoRepository todoRepository;

    @BeforeEach
    void setup() {
        todoRepository.deleteAll();
        Todo todoTestModel = Todo.builder().title("Read the book").build();
        todoRepository.save(todoTestModel);
    }

    @Test
    void getTodos_shouldReturnOkAndJsonList()  throws Exception {
        mockMvc.perform(get("/api/todo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getTodos_withMockedData_shouldReturnCorrectTitle() throws Exception {
        when(todoRepository.findAll())
                .thenReturn(
                        List.of(
                                new Todo(1L, "Task 1", false)
                        )
                );
        mockMvc.perform(get("/api/todo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Task 1"));
    }

    @Test
    void createTodo_withValidBody_shouldReturnCreatedTodo() throws Exception {
        String req = """
                { "title":"Reading Book" }
                """;

        Todo savedTodo = new Todo(1L, "Reading Book", false);

        when(todoRepository.save(any(Todo.class)))
                .thenReturn(savedTodo);

        mockMvc.perform(post("/api/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(req))
                .andExpect(status().is(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Reading Book"))
                .andExpect(jsonPath("$.completed").value(false));

    }

    @Test
    void createTodo_titleNotBlank_notEmpty() throws Exception {
        String reqEmptyTitle = """
                { "title": "" }
                """;

        mockMvc.perform(post("/api/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqEmptyTitle))
                .andExpect(status().isBadRequest());

        String reqNullTitle = """
                { "title": null }
                """;

        mockMvc.perform(post("/api/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqNullTitle))
                .andExpect(status().isBadRequest());

    }

}