package com.example.springmvcmybatis.service;


import com.example.springmvcmybatis.dto.Tag;
import com.example.springmvcmybatis.dto.TagWithTodos;

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
