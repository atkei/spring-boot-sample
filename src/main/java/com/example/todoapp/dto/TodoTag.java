package com.example.todoapp.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TodoTag {
    private long todoId;
    private String tagName;
}
