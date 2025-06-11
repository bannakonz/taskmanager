package com.myapp.taskmanager.repository;

import com.myapp.taskmanager.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {}