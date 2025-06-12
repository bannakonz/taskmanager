package com.myapp.taskmanager.service;

import com.myapp.taskmanager.entity.Todo;
import com.myapp.taskmanager.repository.TodoRepository;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test") // Uses application-test.properties if it exists
@Transactional // Rolls back after each test to keep tests isolated
class TodoServiceIntegrationTest {

    @Autowired
    private TodoService todoService;

    @Autowired
    private TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        todoRepository.deleteAll();
    }

    @Test
    void getAllTodo_withEmptyDatabase_shouldReturnEmptyList() {
        // When
        List<Todo> todos = todoService.getAllTodo();

        // Then
        assertTrue(todos.isEmpty());
    }

    @Test
    void getAllTodo_withExistingTodos_shouldReturnAllTodos() {
        // Given - Insert test data directly to database
        Todo todo1 = todoRepository.save(Todo.builder()
                .title("Learn Spring Boot")
                .completed(false)
                .build());

        Todo todo2 = todoRepository.save(Todo.builder()
                .title("Write Tests")
                .completed(true)
                .build());

        // When
        List<Todo> todos = todoService.getAllTodo();

        // Then
        assertEquals(2, todos.size());

        // Verify both todos are returned
        assertTrue(todos.stream().anyMatch(t -> t.getTitle().equals("Learn Spring Boot")));
        assertTrue(todos.stream().anyMatch(t -> t.getTitle().equals("Write Tests")));
        assertTrue(todos.stream().anyMatch(t -> t.isCompleted()));
        assertTrue(todos.stream().anyMatch(t -> !t.isCompleted()));
    }

    @Test
    void createTodo_shouldPersistToDatabase() {
        // Given
        Todo newTodo = Todo.builder()
                .title("Integration Test Todo")
                .completed(false)
                .build();

        // When
        Todo savedTodo = todoService.createTodo(newTodo);

        // Then
        assertNotNull(savedTodo);
        assertNotNull(savedTodo.getId());
        assertEquals("Integration Test Todo", savedTodo.getTitle());
        assertFalse(savedTodo.isCompleted());

        // Verify it's actually in the database
        Optional<Todo> fromDb = todoRepository.findById(savedTodo.getId());
        assertTrue(fromDb.isPresent());
        assertEquals("Integration Test Todo", fromDb.get().getTitle());
    }

    @Test
    void createTodo_withCompletedTrue_shouldSaveCorrectly() {
        // Given
        Todo newTodo = Todo.builder()
                .title("Completed Task")
                .completed(true)
                .build();

        // When
        Todo savedTodo = todoService.createTodo(newTodo);

        // Then
        assertNotNull(savedTodo);
        assertTrue(savedTodo.isCompleted());

        // Verify in database
        Optional<Todo> fromDb = todoRepository.findById(savedTodo.getId());
        assertTrue(fromDb.isPresent());
        assertTrue(fromDb.get().isCompleted());
    }

    @Test
    void updateTodo_withExistingTodo_shouldUpdateInDatabase() throws BadRequestException {
        // Given - Create initial todo
        Todo originalTodo = todoRepository.save(Todo.builder()
                .title("Original Title")
                .completed(false)
                .build());

        Todo updateData = Todo.builder()
                .title("Updated Title")
                .completed(true)
                .build();

        // When
        Todo updatedTodo = todoService.updateTodo(originalTodo.getId(), updateData);

        // Then
        assertNotNull(updatedTodo);
        assertEquals(originalTodo.getId(), updatedTodo.getId()); // ID should remain same
        assertEquals("Updated Title", updatedTodo.getTitle());
        assertTrue(updatedTodo.isCompleted());

        // Verify changes are persisted in database
        Optional<Todo> fromDb = todoRepository.findById(originalTodo.getId());
        assertTrue(fromDb.isPresent());
        assertEquals("Updated Title", fromDb.get().getTitle());
        assertTrue(fromDb.get().isCompleted());
    }

    @Test
    void updateTodo_withNonExistentId_shouldThrowBadRequestException() {
        // Given
        Long nonExistentId = 999L;
        Todo updateData = Todo.builder()
                .title("Should Not Save")
                .completed(true)
                .build();

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            todoService.updateTodo(nonExistentId, updateData);
        });

        // Verify no new to-do was created
        List<Todo> allTodos = todoRepository.findAll();
        assertTrue(allTodos.isEmpty());
    }

    @Test
    void updateTodo_onlyTitleChange_shouldPreserveOtherFields() throws BadRequestException {
        // Given
        Todo originalTodo = todoRepository.save(Todo.builder()
                .title("Original Title")
                .completed(true)
                .build());

        Todo updateData = Todo.builder()
                .title("New Title")
                .completed(true) // Same as original
                .build();

        // When
        Todo updatedTodo = todoService.updateTodo(originalTodo.getId(), updateData);

        // Then
        assertEquals("New Title", updatedTodo.getTitle());
        assertTrue(updatedTodo.isCompleted()); // Should remain true
        assertEquals(originalTodo.getId(), updatedTodo.getId());
    }

    @Test
    void updateTodo_onlyCompletedChange_shouldPreserveTitle() throws BadRequestException {
        // Given
        Todo originalTodo = todoRepository.save(Todo.builder()
                .title("Important Task")
                .completed(false)
                .build());

        Todo updateData = Todo.builder()
                .title("Important Task") // Same as original
                .completed(true) // Changed
                .build();

        // When
        Todo updatedTodo = todoService.updateTodo(originalTodo.getId(), updateData);

        // Then
        assertEquals("Important Task", updatedTodo.getTitle());
        assertTrue(updatedTodo.isCompleted());
        assertEquals(originalTodo.getId(), updatedTodo.getId());
    }

    @Test
    void fullWorkflow_createUpdateAndRetrieve_shouldWorkCorrectly() throws BadRequestException {
        // Given - Create a to-do
        Todo newTodo = Todo.builder()
                .title("Workflow Test")
                .completed(false)
                .build();

        // When - Create to-do
        Todo createdTodo = todoService.createTodo(newTodo);
        assertNotNull(createdTodo.getId());

        // When - Update to-do
        Todo updateData = Todo.builder()
                .title("Updated Workflow Test")
                .completed(true)
                .build();
        Todo updatedTodo = todoService.updateTodo(createdTodo.getId(), updateData);

        // When - Get all todos
        List<Todo> allTodos = todoService.getAllTodo();

        // Then - Verify complete workflow
        assertEquals(1, allTodos.size());
        Todo finalTodo = allTodos.get(0);
        assertEquals(createdTodo.getId(), finalTodo.getId());
        assertEquals("Updated Workflow Test", finalTodo.getTitle());
        assertTrue(finalTodo.isCompleted());
    }

    @Test
    void multipleOperations_shouldMaintainDataConsistency() throws BadRequestException {
        // Given - Create multiple todos
        Todo todo1 = todoService.createTodo(Todo.builder()
                .title("Task 1")
                .completed(false)
                .build());

        Todo todo2 = todoService.createTodo(Todo.builder()
                .title("Task 2")
                .completed(false)
                .build());

        Todo todo3 = todoService.createTodo(Todo.builder()
                .title("Task 3")
                .completed(true)
                .build());

        // When - Update one todo
        todoService.updateTodo(todo2.getId(), Todo.builder()
                .title("Updated Task 2")
                .completed(true)
                .build());

        // When - Get all todos
        List<Todo> allTodos = todoService.getAllTodo();

        // Then - Verify data consistency
        assertEquals(3, allTodos.size());

        // Count completed vs incomplete
        long completedCount = allTodos.stream().filter(Todo::isCompleted).count();
        long incompleteCount = allTodos.stream().filter(t -> !t.isCompleted()).count();

        assertEquals(2, completedCount); // todo2 and todo3 should be completed
        assertEquals(1, incompleteCount); // only todo1 should be incomplete

        // Verify specific updates
        Optional<Todo> updatedTodo2 = allTodos.stream()
                .filter(t -> t.getId().equals(todo2.getId()))
                .findFirst();
        assertTrue(updatedTodo2.isPresent());
        assertEquals("Updated Task 2", updatedTodo2.get().getTitle());
        assertTrue(updatedTodo2.get().isCompleted());
    }
}