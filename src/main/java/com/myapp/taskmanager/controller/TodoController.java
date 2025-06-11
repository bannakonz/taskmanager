package com.myapp.taskmanager.controller;

import com.myapp.taskmanager.entity.Todo;
import com.myapp.taskmanager.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        return ResponseEntity.ok(data);
    }

    @PostMapping("todo")
    public ResponseEntity<Todo> createData(@RequestBody Todo reqCreateTodo) {
        Todo todo = todoService.createTodo(reqCreateTodo);
        log.info("Created todo controller: {}", todo);
        return ResponseEntity.ok(todo);
    }

}
