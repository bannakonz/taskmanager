package com.myapp.taskmanager.service;

import com.myapp.taskmanager.entity.Todo;
import com.myapp.taskmanager.repository.TodoRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TodoServiceTest {
    @InjectMocks
    private TodoService todoService;

    @Mock
    private TodoRepository todoRepository;

    @Test
    void getAllDataShouldBeReturnEmpty() {
        List<Todo> allData =  todoService.getAllTodo();

        Assertions.assertThat(allData).isEmpty();
    }

    @Test
    void shouldReturnListOfDataSize() {
        when(todoRepository.findAll()).thenReturn(List.of(new Todo(), new Todo()));

        List<Todo> data = todoService.getAllTodo();

        Assertions.assertThat(data).hasSize(2);
    }

    @Test
    void shouldCreateTodoSuccessfully() {
        Todo todoToCreate = new Todo();
        todoToCreate.setTitle("New Todo");
        todoToCreate.setCompleted(false);

        Todo todoWithId = new Todo(1L, "New Todo", false);
        when(todoRepository.save(todoToCreate)).thenReturn(todoWithId);

        Todo createdTodo = todoService.createTodo(todoToCreate);

        Assertions.assertThat(createdTodo).isNotNull();
        Assertions.assertThat(createdTodo.getId()).isNotNull();
        Assertions.assertThat(createdTodo.getTitle()).isEqualTo("New Todo");
        Assertions.assertThat(createdTodo.isCompleted()).isFalse();
    }
}