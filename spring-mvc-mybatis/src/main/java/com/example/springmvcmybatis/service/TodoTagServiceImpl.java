package com.example.springmvcmybatis.service;

import com.example.springmvcmybatis.dto.Todo;
import com.example.springmvcmybatis.dto.TodoTag;
import com.example.springmvcmybatis.dto.TodoWithTags;
import com.example.springmvcmybatis.exception.RestException;
import com.example.springmvcmybatis.mapper.TagMapper;
import com.example.springmvcmybatis.mapper.TodoMapper;
import com.example.springmvcmybatis.mapper.TodoTagMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoTagServiceImpl implements TodoTagService {
    private final TodoMapper todoMapper;
    private final TagMapper tagMapper;
    private final TodoTagMapper todoTagMapper;


    @Override
    @Transactional
    public TodoWithTags insertTodoWithTags(TodoWithTags todoWithTags) {
        Todo todo = Todo.builder()
                .title(todoWithTags.getTitle())
                .description(todoWithTags.getDescription())
                .completed(todoWithTags.isCompleted())
                .build();

        todoMapper.insert(todo);
        long todoId = todo.getTodoId();
        todoWithTags.setTodoId(todoId);

        try {
            todoWithTags.getTags().forEach(tag -> addTag(todoId, tag.getName()));
        } catch (RestException e) {
            throw new RestException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return todoWithTags;
    }

    @Override
    public List<TodoWithTags> listTodosWithTags(Boolean completed, int limit, long offset) {
        return todoMapper.listWithTags(completed, limit, offset);
    }

    @Override
    public TodoWithTags getTodoWithTags(long todoId) {
        Optional<TodoWithTags> todoWithTagsOpt = todoMapper.getWithTags(todoId);
        if (todoWithTagsOpt.isPresent()) {
            return todoWithTagsOpt.get();
        } else {
            throw new RestException(HttpStatus.NOT_FOUND, "No such todo: " + todoId);
        }
    }

    @Override
    @Transactional
    public void addTag(long todoId, String tagName) {
        tagMapper.get(tagName).orElseThrow(() ->
                new RestException(HttpStatus.BAD_REQUEST, "No such tag: " + tagName));
        todoMapper.get(todoId).orElseThrow(() ->
                new RestException(HttpStatus.NOT_FOUND, "No such todo: " + todoId));
        todoTagMapper.find(todoId, tagName).ifPresent(e -> {
            throw new RestException(HttpStatus.BAD_REQUEST,
                    String.format("Todo: %s already has tag: %s", todoId, tagName));
        });

        TodoTag entity = new TodoTag()
                .setTodoId(todoId)
                .setTagName(tagName);

        todoTagMapper.insert(entity);
    }

    @Override
    @Transactional
    public void deleteTag(long todoId, String tagName) {
        todoMapper.get(todoId).orElseThrow(() ->
                new RestException(HttpStatus.NOT_FOUND, "No such todo: " + todoId)
        );

        if (!todoTagMapper.deleteByName(todoId, tagName)) {
            throw new RestException(HttpStatus.BAD_REQUEST,
                    String.format("Todo: %s doesn't have tag: %s", todoId, tagName));
        }
    }
}
