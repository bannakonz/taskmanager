package com.myapp.taskmanager.controller;

import com.myapp.taskmanager.entity.Todo;
import com.myapp.taskmanager.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor // *A ใส่เพื่อไม่ให้ตอนเรียก สร้าง todoService error
public class TodoController {

    private final TodoService todoService; // *A

    @GetMapping("todo")
    public ResponseEntity<List<Todo>> getAllData() {
        List<Todo> data = todoService.getAllTodo();
        log.info("@GetMapping(\"todo\") | List<Todo> data = {}", data);
        return ResponseEntity.ok(data);
    }

    @PostMapping("todo")
    public ResponseEntity<Todo> createData(@Valid @RequestBody Todo reqCreateTodo)  {
        Todo todo = todoService.createTodo(reqCreateTodo);
        log.info("@PostMapping(\"todo\") | @RequestBody Todo reqCreateTodo = {} ", reqCreateTodo);
        log.info("@PostMapping(\"todo\") | Todo todo = {}", todo);
        log.info("ResponseEntity.ok(todo) {}", ResponseEntity.ok(todo)); // <200 OK OK,Todo(id=1, title=null, completed=false),[]>
        return ResponseEntity.ok(todo);
    }

    @PutMapping("todo/{id}")
    public ResponseEntity<Todo> updateData(@PathVariable Long id, @RequestBody Todo reqUpdateTodo) throws BadRequestException {
        Todo todo = todoService.updateTodo(id, reqUpdateTodo);
        return ResponseEntity.ok(todo);
    }

}
