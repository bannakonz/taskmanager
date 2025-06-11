package com.myapp.taskmanager.service;

import com.myapp.taskmanager.entity.Todo;
import com.myapp.taskmanager.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {

    @Autowired
    TodoRepository todoRepository;

    public List<Todo> getAllTodo() {
        return todoRepository.findAll();
    }
}
