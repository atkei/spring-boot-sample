package com.example.springmvcmybatis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
public class TodoWithTags extends Todo {
    @NotEmpty(message = "must not be empty")
    @Schema(name = "Todo tags")
    private List<Tag> tags = new ArrayList<>();

    @Override
    public String toString() {
        return "TodoWithTags(" + super.toString() + ", tags=" + tags.toString() + ")";
    }
}
