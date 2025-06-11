package com.myapp.taskmanager.service;

import com.myapp.taskmanager.entity.Todo;
import com.myapp.taskmanager.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor // *B, add RequiredArgsConstructor,  when add *B
public class TodoService {

//    @Autowired // prevent get all first time (no data) มั้ง
    private final TodoRepository todoRepository; // *B

    public List<Todo> getAllTodo() {
        List<Todo> todos = todoRepository.findAll();
        log.info("get all todo ==> {}", todos);
        return todos;
    }

    public Todo createTodo(Todo todo) {
        log.info("payload create todo ==> {}", todo);
        todoRepository.save(todo);

        return todo;
    }

    public Todo updateTodo(Long id, Todo todo) throws BadRequestException {
        Optional<Todo>  todoOptional = todoRepository.findById(id);
        if (todoOptional.isPresent()) {
              Todo todoGet = todoOptional.get();
              todoGet.setTitle(todo.getTitle());
              todoGet.setCompleted(todo.isCompleted());
              todoRepository.save(todoGet);
              return todoGet;
        } else {
            throw new BadRequestException();
        }
    }
}
