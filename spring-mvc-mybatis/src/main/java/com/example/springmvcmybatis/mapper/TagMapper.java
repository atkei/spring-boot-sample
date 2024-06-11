package com.example.springmvcmybatis.mapper;

import com.example.springmvcmybatis.dto.Tag;
import com.example.springmvcmybatis.dto.TagWithTodos;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface TagMapper {
    void insert(Tag entity);

    List<Tag> list(int limit, long offset);

    List<TagWithTodos> listWithTodos(int limit, long offset);

    Optional<Tag> get(String name);

    Optional<TagWithTodos> getWithTodos(String name);

    boolean delete(String name);

    void deleteAll();
}
