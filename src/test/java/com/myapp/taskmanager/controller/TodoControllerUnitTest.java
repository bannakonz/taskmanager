package com.myapp.taskmanager.controller;

import com.myapp.taskmanager.entity.Todo;
import com.myapp.taskmanager.service.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
class TodoControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService todoService;

    @Test
    void getTodos_shouldReturnOkAndJsonList() throws Exception {
        // Arrange
        Todo mockTodo = Todo.builder()
                .id(1L)
                .title("Reading Book")
                .completed(false)
                .build();

        List<Todo> mockTodos = Collections.singletonList(mockTodo);
        when(todoService.getAllTodo()).thenReturn(mockTodos);

        // Act & Assert
        mockMvc.perform(get("/api/todo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Reading Book"))
                .andExpect(jsonPath("$[0].completed").value(false));
    }

    @Test
    void getTodos_withDifferentData_shouldReturnCorrectTitle() throws Exception {
        // Arrange
        Todo mockTodo = Todo.builder()
                .id(1L)
                .title("Task 1")
                .completed(false)
                .build();

        List<Todo> mockTodos = Collections.singletonList(mockTodo);
        when(todoService.getAllTodo()).thenReturn(mockTodos);

        // Act & Assert
        mockMvc.perform(get("/api/todo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[0].completed").value(false));
    }

    @Test
    void createTodo_withValidBody_shouldReturnCreatedTodo() throws Exception {
        // Arrange
        Todo mockCreatedTodo = Todo.builder()
                .id(1L)
                .title("New Task")
                .completed(false)  // Assuming service sets default completed to false
                .build();

        when(todoService.createTodo(any(Todo.class))).thenReturn(mockCreatedTodo);

        String requestJson = """
                { "title":"New Task" }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getTodos_whenServiceReturnsEmptyList_shouldReturnEmptyArray() throws Exception {
        // Arrange
        when(todoService.getAllTodo()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/todo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getTodos_whenServiceReturnsMultipleTodos_shouldReturnAllTodos() throws Exception {
        // Arrange
        Todo todo1 = Todo.builder()
                .id(1L)
                .title("Task 1")
                .completed(false)
                .build();

        Todo todo2 = Todo.builder()
                .id(2L)
                .title("Task 2")
                .completed(true)
                .build();

        List<Todo> mockTodos = Arrays.asList(todo1, todo2);
        when(todoService.getAllTodo()).thenReturn(mockTodos);

        // Act & Assert
        mockMvc.perform(get("/api/todo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[0].completed").value(false))
                .andExpect(jsonPath("$[1].title").value("Task 2"))
                .andExpect(jsonPath("$[1].completed").value(true));
    }
}

