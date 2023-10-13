package com.example.todoapp.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class TagWithTodos {
    private String name;
    private List<Todo> todos;
}
