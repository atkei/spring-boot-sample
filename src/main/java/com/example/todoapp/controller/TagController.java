package com.example.todoapp.controller;

import com.example.todoapp.dto.Tag;
import com.example.todoapp.dto.TagWithTodos;
import com.example.todoapp.service.TagService;
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
@RequestMapping(path = "/v1/tags")
public class TagController {
    private final TagService tagService;

    @PostMapping(path="")
    Tag insertTag(@RequestBody @Validated Tag tag) {
        return tagService.insertTag(tag);
    }

    @GetMapping(path="")
    List<Tag> listTags(
            @RequestParam(name = "limit", defaultValue = "100") @Range(min = 0, max = Integer.MAX_VALUE) int limit,
            @RequestParam(name = "offset", defaultValue = "0") @Range(min = 0) long offset
    ) {
        return tagService.listTags(limit, offset);
    }

    @GetMapping(path="/todos")
    List<TagWithTodos> listTagsWithTodos(
            @RequestParam(name = "limit", defaultValue = "100") @Range(min = 0, max = Integer.MAX_VALUE) int limit,
            @RequestParam(name = "offset", defaultValue = "0") @Range(min = 0) long offset
    ) {
        return tagService.listTagsWithTodos(limit, offset);
    }

    @GetMapping(path="{name}")
    Tag getTag(@PathVariable String name) {
        return tagService.getTag(name);
    }

    @GetMapping(path="{name}/todos")
    TagWithTodos getTagWithTodos(@PathVariable String name) {
        return tagService.getTagWithTodos(name);
    }

    @DeleteMapping(path="{name}")
    String deleteTag(@PathVariable String name) {
        tagService.deleteTag(name);
        return "{}";
    }
}
