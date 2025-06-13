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
        log.info("TodoService get all todos ==> {}", todos);
        return todos;
    }

    public Todo createTodo(Todo todo) throws BadRequestException {
        log.info("TodoService createTodo(Todo todo) ==> {}", todo); // id ยังเป็น null เพราะยังไม่ลงฐานข้อมูล, และถ้าส่ง title เป็น null ตรงนี้ยังคงเป็น null
        if (todo.getTitle() == null || todo.getTitle().trim().isEmpty()) {
            log.info("Title is null or empty, throwing BadRequestException");
            throw new BadRequestException("Title cannot be null or empty");
        }
        todoRepository.save(todo);
        log.info("TodoService createTodo(Todo todo) 2 ==> {}", todo); // id เป็น 1 เพราะถูก save ลงฐานข้อมูลแล้ว | Todo(id=1, title=null, completed=false)
        return todo;
    }

    public Todo updateTodo(Long id, Todo todo) throws BadRequestException {
        log.info("updateTodo(Long id) ==> {} ", id);
        log.info("updateTodo(Todo todo) ==> {}", todo);
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
