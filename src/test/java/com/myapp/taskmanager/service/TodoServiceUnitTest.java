package com.myapp.taskmanager.service;

import com.myapp.taskmanager.entity.Todo;
import com.myapp.taskmanager.repository.TodoRepository;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceUnitTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private Todo testTodo;
    private Todo savedTodo;

    @BeforeEach
    void setUp() {
        testTodo = Todo.builder()
                .title("Test Todo")
                .completed(false)
                .build();

        savedTodo = Todo.builder()
                .id(1L)
                .title("Test Todo")
                .completed(false)
                .build();
    }

    @Test
    void getAllTodo_shouldReturnAllTodos() {
        // Given
        List<Todo> expectedTodos = Arrays.asList(
                new Todo(1L, "Todo 1", false),
                new Todo(2L, "Todo 2", true)
        );
        when(todoRepository.findAll()).thenReturn(expectedTodos);

        // When
        List<Todo> actualTodos = todoService.getAllTodo();

        // Then
        assertEquals(2, actualTodos.size());
        assertEquals(expectedTodos, actualTodos);
        verify(todoRepository, times(1)).findAll();
    }

    @Test
    void getAllTodo_whenNoTodos_shouldReturnEmptyList() {
        // Given
        when(todoRepository.findAll()).thenReturn(List.of());

        // When
        List<Todo> actualTodos = todoService.getAllTodo();

        // Then
        assertTrue(actualTodos.isEmpty());
        verify(todoRepository, times(1)).findAll();
    }

    @Test
    void createTodo_shouldSaveAndReturnTodo() {
        // Given
        when(todoRepository.save(any(Todo.class))).thenReturn(savedTodo);

        // When
        Todo result = todoService.createTodo(testTodo);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Todo", result.getTitle());
        assertFalse(result.isCompleted());
        verify(todoRepository, times(1)).save(testTodo);
    }

    @Test
    void createTodo_withNullTodo_shouldHandleGracefully() {
        // Given
        when(todoRepository.save(any(Todo.class))).thenReturn(null);

        // When
        Todo result = todoService.createTodo(testTodo);

        // Then
        assertNull(result);
        verify(todoRepository, times(1)).save(testTodo);
    }

    @Test
    void updateTodo_whenTodoExists_shouldUpdateAndReturnTodo() throws BadRequestException {
        // Given
        Long todoId = 1L;
        Todo existingTodo = new Todo(1L, "Old Title", false);
        Todo updateData = new Todo(null, "New Title", true);

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(existingTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(existingTodo);

        // When
        Todo result = todoService.updateTodo(todoId, updateData);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Title", result.getTitle());
        assertTrue(result.isCompleted());

        verify(todoRepository, times(1)).findById(todoId);
        verify(todoRepository, times(1)).save(existingTodo);
    }

    @Test
    void updateTodo_whenTodoNotFound_shouldThrowBadRequestException() {
        // Given
        Long todoId = 999L;
        Todo updateData = new Todo(null, "New Title", true);

        when(todoRepository.findById(todoId)).thenReturn(Optional.empty());

        // When & Then
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> todoService.updateTodo(todoId, updateData)
        );

        verify(todoRepository, times(1)).findById(todoId);
        verify(todoRepository, never()).save(any(Todo.class));
    }

    @Test
    void updateTodo_shouldOnlyUpdateTitleAndCompleted() throws BadRequestException {
        // Given
        Long todoId = 1L;
        Todo existingTodo = new Todo(1L, "Old Title", false);
        Todo updateData = new Todo(999L, "New Title", true); // ID should be ignored

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(existingTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(existingTodo);

        // When
        Todo result = todoService.updateTodo(todoId, updateData);

        // Then
        assertEquals(1L, result.getId()); // Original ID preserved
        assertEquals("New Title", result.getTitle()); // Title updated
        assertTrue(result.isCompleted()); // Completed updated

        verify(todoRepository, times(1)).findById(todoId);
        verify(todoRepository, times(1)).save(existingTodo);
    }

    @Test
    void updateTodo_withPartialUpdate_shouldUpdateOnlyProvidedFields() throws BadRequestException {
        // Given
        Long todoId = 1L;
        Todo existingTodo = new Todo(1L, "Old Title", false);
        Todo updateData = new Todo(null, "New Title", false);

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(existingTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(existingTodo);

        // When
        Todo result = todoService.updateTodo(todoId, updateData);

        // Then
        assertEquals(1L, result.getId());
        assertEquals("New Title", result.getTitle());
        assertFalse(result.isCompleted()); // Should remain false

        verify(todoRepository, times(1)).findById(todoId);
        verify(todoRepository, times(1)).save(existingTodo);
    }
}