package com.example.springmvcmybatis.mapper;

import com.example.springmvcmybatis.dto.TodoTag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface TodoTagMapper {
    void insert(TodoTag entity);

    List<TodoTag> list(int limit, long offset);

    Optional<TodoTag> get(long id);

    Optional<TodoTag> find(long todoId, String tagName);

    boolean delete(long id);

    boolean deleteByName(long todoId, String tagName);
}
