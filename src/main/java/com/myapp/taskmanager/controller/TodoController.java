package com.myapp.taskmanager.controller;

import com.myapp.taskmanager.entity.Todo;
import com.myapp.taskmanager.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor // *A ใส่เพื่อไม่ให้ตอนเรียก สร้าง todoService error
public class TodoController {

    private final TodoService todoService; // *A

    @GetMapping("todo")
    public ResponseEntity<List<Todo>> getDataAllTodo() {
        List<Todo> data = todoService.getAllTodo();
        return ResponseEntity.ok(data);
    }


}
