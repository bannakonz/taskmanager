package com.myapp.taskmanager.service;

import com.myapp.taskmanager.entity.Todo;
import com.myapp.taskmanager.repository.TodoRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TodoService {

    @Autowired
    TodoRepository todoRepository;

    public List<Todo> getAllTodo() {
        return todoRepository.findAll();
    }

    public Todo createTodo(Todo todo) {
        todoRepository.save(todo);
        log.info("Created todo: {}", todo);

        return todo;
    }

    public Todo updateTodo(Long id, Todo todo) throws BadRequestException {
        Optional<Todo>  todoOptional = todoRepository.findById(id);
        if (todoOptional.isPresent()) {
              Todo todoGet = todoOptional.get();
              todoGet.setTitle(todo.getTitle());
              todoRepository.save(todoGet);
              return todoGet;
        } else {
            throw new BadRequestException();
        }
    }
}
