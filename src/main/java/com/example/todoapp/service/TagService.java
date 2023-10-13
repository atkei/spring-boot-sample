package com.example.todoapp.service;


import com.example.todoapp.dto.Tag;
import com.example.todoapp.dto.TagWithTodos;

import java.util.List;

public interface TagService {
    Tag insertTag(Tag tag);

    List<Tag> listTags(int limit, long offset);

    List<TagWithTodos> listTagsWithTodos(int limit, long offset);

    Tag getTag(String name);
    TagWithTodos getTagWithTodos(String name);

    void deleteTag(String name);

    void deleteAllTags();
}
