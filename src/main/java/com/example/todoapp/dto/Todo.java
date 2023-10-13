package com.example.todoapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@Accessors(chain = true)
@NoArgsConstructor
public class Todo {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private long todoId;

    @NotBlank(message = "must not be blank")
    @Schema(defaultValue = "title")
    private String title;

    @Schema(defaultValue = "description")
    private String description = "";

    @Schema(defaultValue = "false")
    private boolean completed = false;
}
