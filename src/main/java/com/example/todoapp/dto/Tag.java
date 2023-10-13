package com.example.todoapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class Tag {
    @NotBlank(message = "must not be blank")
    @Schema(defaultValue = "name")
    private String name;
}
