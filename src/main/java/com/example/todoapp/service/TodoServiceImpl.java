package com.example.todoapp.service;

import com.example.todoapp.dto.Todo;
import com.example.todoapp.exception.RestException;
import com.example.todoapp.mapper.TodoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {
    private final TodoMapper todoMapper;

    @Override
    @Transactional
    public Todo insertTodo(Todo todo) {
        todoMapper.insert(todo);
        return todo;
    }

    @Override
    public List<Todo> listTodos(int limit, long offset, Boolean completed) {
        return todoMapper.list(limit, offset, completed);
    }

    @Override
    public Todo getTodo(long todoId) {
        Optional<Todo> todoOpt = todoMapper.get(todoId);
        if (todoOpt.isPresent()) {
            return todoOpt.get();
        } else {
            throw new RestException(HttpStatus.NOT_FOUND, "No such todo: " + todoId);
        }
    }

    @Override
    @Transactional
    public void updateTodo(long todoId, Todo todo) {
        if(!todoMapper.update(todo.setTodoId(todoId))) {
            throw new RestException(HttpStatus.NOT_FOUND, "No such todo: " + todoId);
        }
    }

    @Override
    @Transactional
    public void deleteTodo(long todoId) {
        if(!todoMapper.delete(todoId)) {
            throw new RestException(HttpStatus.NOT_FOUND, "No such todo: " + todoId);
        }
    }


    @Override
    @Transactional
    public void deleteAllTodos() {
        todoMapper.deleteAll();
    }
}
