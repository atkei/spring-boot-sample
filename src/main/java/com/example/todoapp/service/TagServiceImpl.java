package com.example.todoapp.service;

import com.example.todoapp.dto.Tag;
import com.example.todoapp.dto.TagWithTodos;
import com.example.todoapp.exception.RestException;
import com.example.todoapp.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagMapper tagMapper;

    @Override
    @Transactional
    public Tag insertTag(Tag tag) {
        if (tagMapper.get(tag.getName()).isPresent()) {
            throw new RestException(HttpStatus.CONFLICT, String.format("Tag: %s already exists", tag.getName()));
        }
        tagMapper.insert(tag);
        return tag;
    }

    @Override
    public List<Tag> listTags(int limit, long offset) {
        return tagMapper.list(limit, offset);
    }

    @Override
    public List<TagWithTodos> listTagsWithTodos(int limit, long offset) {
        return tagMapper.listWithTodos(limit, offset);
    }

    @Override
    public Tag getTag(String name) {
        Optional<Tag> tagOpt = tagMapper.get(name);
        if (tagOpt.isPresent()) {
            return tagOpt.get();
        } else {
            throw new RestException(HttpStatus.NOT_FOUND, "No such tag: " + name);
        }
    }

    @Override
    public TagWithTodos getTagWithTodos(String name) {
        Optional<TagWithTodos> tagOpt = tagMapper.getWithTodos(name);
        if (tagOpt.isPresent()) {
            return tagOpt.get();
        } else {
            throw new RestException(HttpStatus.NOT_FOUND, "No such tag: " + name);
        }
    }

    @Override
    @Transactional
    public void deleteTag(String name) {
        if (!tagMapper.delete(name)) {
            throw new RestException(HttpStatus.NOT_FOUND, "No such tag: " + name);
        }
    }

    @Override
    @Transactional
    public void deleteAllTags() {
        tagMapper.deleteAll();
    }
}
