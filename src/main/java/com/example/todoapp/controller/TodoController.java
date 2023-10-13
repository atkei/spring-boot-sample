package com.example.todoapp.controller;

import com.example.todoapp.dto.Todo;
import com.example.todoapp.dto.TodoWithTags;
import com.example.todoapp.service.TodoService;
import com.example.todoapp.service.TodoTagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/v1/todos")
public class TodoController {
    private final TodoService todoService;
    private final TodoTagService todoTagService;

    @PostMapping(path="")
    Todo insertTodo(@RequestBody @Validated Todo todo) {
        return todoService.insertTodo(todo);
    }

    @PostMapping(path="/tags")
    TodoWithTags insertTodoWithTags(@RequestBody @Validated TodoWithTags todoWithTags) {
        return todoTagService.insertTodoWithTags(todoWithTags);
    }

    @GetMapping(path="")
    List<Todo> listTodos(
            @RequestParam(name = "completed", required = false) Boolean completed,
            @RequestParam(name = "limit", defaultValue = "100") @Range(min = 0, max = Integer.MAX_VALUE) int limit,
            @RequestParam(name = "offset", defaultValue = "0") @Range(min = 0) long offset
    ) {
        return todoService.listTodos(limit, offset, completed);
    }

    @GetMapping(path="/tags")
    List<TodoWithTags> listTodosWithTags(
            @RequestParam(name = "completed", required = false) Boolean completed,
            @RequestParam(name = "limit", defaultValue = "100") @Range(min = 0, max = Integer.MAX_VALUE) int limit,
            @RequestParam(name = "offset", defaultValue = "0") @Range(min = 0) long offset
    ) {
        return todoTagService.listTodosWithTags(completed, limit, offset);
    }

    @GetMapping(path="{todoId}")
    Todo getTodo(@PathVariable long todoId) {
        return todoService.getTodo(todoId);
    }

    @GetMapping(path="{todoId}/tags")
    TodoWithTags getTodoWithTags(@PathVariable long todoId) {
        return todoTagService.getTodoWithTags(todoId);
    }

    @PutMapping(path="{todoId}")
    String updateTodo(@PathVariable long todoId, @RequestBody @Validated Todo todo) {
        todoService.updateTodo(todoId, todo);
        return "{}";
    }

    @PutMapping(path="{todoId}/tags/{name}")
    String addTag(@PathVariable long todoId, @PathVariable String name) {
        todoTagService.addTag(todoId, name);
        return "{}";
    }

    @DeleteMapping(path="{todoId}")
    String deleteTodo(@PathVariable long todoId) {
        todoService.deleteTodo(todoId);
        return "{}";
    }

    @DeleteMapping(path="{todoId}/tags/{tagName}")
    String deleteTag(@PathVariable long todoId, @PathVariable String tagName) {
        todoTagService.deleteTag(todoId, tagName);
        return "{}";
    }
}
