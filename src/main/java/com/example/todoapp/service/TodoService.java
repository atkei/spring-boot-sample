package com.example.todoapp.service;


import com.example.todoapp.dto.Todo;

import java.util.List;

public interface TodoService {
    Todo insertTodo(Todo todo);

    List<Todo> listTodos(int limit, long offset, Boolean completed);

    Todo getTodo(long todoId);

    void updateTodo(long todoId, Todo req);

    void deleteTodo(long todoId);

    void deleteAllTodos();
}
