package com.example.springmvcmybatis.controller;

import com.example.springmvcmybatis.dto.Error;
import com.example.springmvcmybatis.dto.Tag;
import com.example.springmvcmybatis.dto.Todo;
import com.example.springmvcmybatis.dto.TodoWithTags;
import com.example.springmvcmybatis.exception.RestException;
import com.example.springmvcmybatis.service.TagService;
import com.example.springmvcmybatis.service.TodoService;
import com.example.springmvcmybatis.service.TodoTagService;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoControllerIT {
    @LocalServerPort
    private int port;

    private String url;

    @Autowired
    private TodoService todoService;

    @Autowired
    private TagService tagService;

    @Autowired
    private TodoTagService todoTagService;

    @Autowired
    private TestRestTemplate restTemplate;

    @PostConstruct
    void postConstruct() {
        url = "http://localhost:" + port + "/v1/todos";
    }

    @BeforeEach
    void setup() {
        todoService.deleteAllTodos();
        tagService.deleteAllTags();
    }

    @Nested
    class CreateTodo {
        @Test
        void createTodo() {
            Todo req = Todo.builder()
                    .title("t1")
                    .description("d1")
                    .build();

            Todo todo = restTemplate.postForObject(url, req, Todo.class);

            assertThat(todo.getTodoId()).isGreaterThan(0L);
            assertThat(todo.getTitle()).isEqualTo(req.getTitle());
            assertThat(todo.getDescription()).isEqualTo(req.getDescription());
            assertThat(todo.isCompleted()).isFalse();
        }

        @Test
        void failCreateTodoNullTitle() {
            HttpEntity<Todo> req = new HttpEntity<>(Todo.builder().description("d1").build());
            ResponseEntity<Error> res = restTemplate.exchange(url, HttpMethod.POST, req, Error.class);

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(Objects.requireNonNull(res.getBody()).message()).isEqualTo("title: must not be blank");
        }

        @Test
        void createTodoWithTags() {
            Tag tag = tagService.insertTag(new Tag().setName("name1"));
            TodoWithTags req = TodoWithTags.builder()
                    .title("t1")
                    .description("d1")
                    .tags(List.of(tag))
                    .build();
            TodoWithTags todoWithTags = restTemplate.postForObject(url + "/tags", req, TodoWithTags.class);

            assertThat(todoWithTags.getTodoId()).isGreaterThan(0L);
            assertThat(todoWithTags.getTitle()).isEqualTo(req.getTitle());
            assertThat(todoWithTags.getDescription()).isEqualTo(req.getDescription());
            assertThat(todoWithTags.isCompleted()).isFalse();
            assertThat(todoWithTags.getTags()).hasSize(1);
            assertThat(todoWithTags.getTags().get(0).getName()).isEqualTo(tag.getName());
        }

        @Test
        void failCreateTodoWithTagsTagNotExist() {
            TodoWithTags req = TodoWithTags.builder()
                    .title("t1")
                    .description("d1")
                    .tags(List.of(new Tag().setName("name1")))
                    .build();

            ResponseEntity<Error> res = restTemplate.exchange(url + "/tags", HttpMethod.POST, new HttpEntity<>(req), Error.class);

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(Objects.requireNonNull(res.getBody()).message()).isEqualTo("No such tag: name1");
            assertThat(res.getBody().errorCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    class GetTodo {
        static Stream<Arguments> getTodosParamsProvider() {
            return Stream.of(
                    // completed, limit, offset, expectedCount, expectedHeadIdx, expectedTailIdx
                    Arguments.arguments(null, 100, 0, 6, 0, 5),
                    Arguments.arguments(null, 2, 1, 2, 1, 2),
                    Arguments.arguments(null, 1, 3, 1, 3, 3),
                    Arguments.arguments(null, 3, 6, 0, -1, -1),
                    Arguments.arguments(true, 100, 0, 2, 1, 3),
                    Arguments.arguments(false, 100, 0, 4, 0, 5),
                    Arguments.arguments(true, 2, 0, 2, 1, 3),
                    Arguments.arguments(true, 2, 1, 1, 3, 3),
                    Arguments.arguments(false, 2, 0, 2, 0, 2)
            );
        }
        @ParameterizedTest
        @MethodSource("getTodosParamsProvider")
        void getTodos(
                final Boolean completed,
                final int limit,
                final int offset,
                final int expectedCount,
                final int expectedHeadIdx,
                final int expectedTailIdx
        ) {
            List<Todo> todos = new ArrayList<>();
            todos.add(todoService.insertTodo(Todo.builder().title("t1").description("d1").build()));
            todos.add(todoService.insertTodo(Todo.builder().title("t2").description("d2").completed(true).build()));
            todos.add(todoService.insertTodo(Todo.builder().title("t3").description("d3").build()));
            todos.add(todoService.insertTodo(Todo.builder().title("t4").description("d4").completed(true).build()));
            todos.add(todoService.insertTodo(Todo.builder().title("t5").description("d5").build()));
            todos.add(todoService.insertTodo(Todo.builder().title("t6").description("d6").build()));

            String reqParams = String.format("?limit=%s&offset=%s", limit, offset);
            if (completed != null) {
                reqParams += String.format("&completed=%s", completed);
            }

            ResponseEntity<List<Todo>> res = restTemplate.exchange(
                    url + reqParams,
                    HttpMethod.GET, null, new ParameterizedTypeReference<>() {}
            );

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
            List<Todo> body = Objects.requireNonNull(res.getBody());
            assertThat(body).hasSize(expectedCount);
            if (expectedCount > 0) {
                assertThat(body.get(0)).isEqualTo(todos.get(expectedHeadIdx));
                assertThat(body.get(expectedCount - 1)).isEqualTo(todos.get(expectedTailIdx));
            }
        }

        @Test
        void getEmptyTodos() {
            ResponseEntity<List<Todo>> res = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {}
            );

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(res.getBody()).isEmpty();
        }

        @Test
        void getTodo() {
            Todo todo = todoService.insertTodo(Todo.builder().title("t1").description("d1").build());

            ResponseEntity<Todo> res = restTemplate.exchange(
                    url + "/" + todo.getTodoId(), HttpMethod.GET, null, Todo.class
            );

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(res.getBody()).isEqualTo(todo);
        }

        @Test
        void failGetTodoNotExist() {
            ResponseEntity<Error> res = restTemplate.exchange(
                    url + "/9999", HttpMethod.GET, null, Error.class);

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(res.getBody().message()).isEqualTo("No such todo: 9999");
            assertThat(res.getBody().errorCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }

        static Stream<Arguments> getTodosWithTagsParamsProvider() {
            return Stream.of(
                    // completed, limit, offset, expectedCount, expectedHeadIdx, expectedTailIdx
                    Arguments.arguments(null, 100, 0, 6, 0, 5),
                    Arguments.arguments(null, 2, 0, 2, 0, 1),
                    Arguments.arguments(null, 1, 3, 1, 3, 3),
                    Arguments.arguments(null, 3, 6, 0, -1, -1),
                    Arguments.arguments(true, 100, 0, 2, 1, 3),
                    Arguments.arguments(false, 100, 0, 4, 0, 5),
                    Arguments.arguments(true, 2, 0, 2, 1, 3),
                    Arguments.arguments(true, 2, 1, 1, 3, 3),
                    Arguments.arguments(false, 2, 0, 2, 0, 2)
            );
        }

        @ParameterizedTest
        @MethodSource("getTodosWithTagsParamsProvider")
        void getTodosWithTags(
                final Boolean completed,
                final int limit,
                final int offset,
                final int expectedCount,
                final int expectedHeadIdx,
                final int expectedTailIdx
        ) {
            List<Tag> tags = Arrays.asList(new Tag().setName("tag1"), new Tag().setName("tag2"), new Tag().setName("tag3"));
            tags.forEach(tag -> {
                tagService.insertTag(tag);
            });

            List<TodoWithTags> todos = new ArrayList<>();
            todos.add(todoTagService.insertTodoWithTags(
                    TodoWithTags.builder()
                            .title("t1")
                            .description("d1")
                            .tags(List.of(tags.get(0)))
                            .build()));
            todos.add(todoTagService.insertTodoWithTags(
                    TodoWithTags.builder()
                            .title("t2")
                            .description("d2")
                            .completed(true)
                            .tags(List.of(tags.get(0), tags.get(1)))
                            .build()));
            todos.add(todoTagService.insertTodoWithTags(
                    TodoWithTags.builder()
                            .title("t3")
                            .description("d3")
                            .tags(List.of(tags.get(0)))
                            .build()));
            todos.add(todoTagService.insertTodoWithTags(
                    TodoWithTags.builder()
                            .title("t4")
                            .description("d4")
                            .completed(true)
                            .tags(List.of(tags.get(2)))
                            .build()));
            todos.add(todoTagService.insertTodoWithTags(
                    TodoWithTags.builder()
                            .title("t5")
                            .description("d5")
                            .tags(Collections.emptyList())
                            .build()));
            todos.add(todoTagService.insertTodoWithTags(
                    TodoWithTags.builder()
                            .title("t6")
                            .description("d6")
                            .tags(tags)
                            .build()));

            String reqParams = String.format("?limit=%s&offset=%s", limit, offset);
            if (completed != null) {
                reqParams += String.format("&completed=%s", completed);
            }

            ResponseEntity<List<TodoWithTags>> res = restTemplate.exchange(
                    url + "/tags" + reqParams,
                    HttpMethod.GET, null, new ParameterizedTypeReference<>() {}
            );

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
            List<TodoWithTags> body = Objects.requireNonNull(res.getBody());
            System.out.println(url + "/tags" + reqParams);
            System.out.println(body);
            assertThat(body).hasSize(expectedCount);
            if (expectedCount > 0) {
                assertThat(body.get(0)).isEqualTo(todos.get(expectedHeadIdx));
                assertThat(body.get(expectedCount - 1)).isEqualTo(todos.get(expectedTailIdx));
            }
        }

        @Test
        void getTodoWithTags() {
            List<Tag> tags = Arrays.asList(new Tag().setName("tag1"), new Tag().setName("tag2"), new Tag().setName("tag3"));
            tags.forEach(tag -> tagService.insertTag(tag));

            TodoWithTags todoWithTags = todoTagService.insertTodoWithTags(
                    TodoWithTags.builder()
                            .title("t1")
                            .description("d1")
                            .tags(tags)
                            .build());

            ResponseEntity<TodoWithTags> res = restTemplate.exchange(
                    url + "/" + todoWithTags.getTodoId() + "/tags", HttpMethod.GET, null, TodoWithTags.class);

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(res.getBody()).isEqualTo(todoWithTags);
        }

        @Test
        void failGetTodoWithTagsNotExist() {
            ResponseEntity<Error> res = restTemplate.exchange(
                    url + "/9999/tags", HttpMethod.GET, null, Error.class);

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(res.getBody().message()).isEqualTo("No such todo: 9999");
            assertThat(res.getBody().errorCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }
    }

    @Nested
    class UpdateTodo {
        @Test
        void updateTodo() {
            long todoId = todoService.insertTodo(Todo.builder().title("t1").description("d1").build()).getTodoId();

            Todo req = Todo.builder()
                    .title("t1_updated")
                    .description("d1_updated")
                    .completed(true)
                    .build();

            ResponseEntity<String> res = restTemplate.exchange(
                    url + "/" + todoId, HttpMethod.PUT, new HttpEntity<>(req), String.class
            );

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(res.getBody()).isEqualTo("{}");
            Todo todo = todoService.getTodo(todoId);
            assertThat(todo.getTodoId()).isEqualTo(todoId);
            assertThat(todo.getTitle()).isEqualTo(req.getTitle());
            assertThat(todo.getDescription()).isEqualTo(req.getDescription());
            assertThat(todo.isCompleted()).isEqualTo(req.isCompleted());
        }

        @Test
        void failUpdateTodoNotExist() {
            Todo req = Todo.builder()
                    .title("t1_updated")
                    .description("d1_updated")
                    .completed(true)
                    .build();

            ResponseEntity<Error> res = restTemplate.exchange(
                    url + "/9999", HttpMethod.PUT, new HttpEntity<>(req), Error.class
            );

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(Objects.requireNonNull(res.getBody()).message()).isEqualTo("No such todo: 9999");
        }
    }

    @Nested
    class DeleteTodo {
        @Test
        void deleteTodo() {
            long todoId = todoService.insertTodo(Todo.builder().title("t1").description("d1").build()).getTodoId();

            ResponseEntity<String> res = restTemplate.exchange(
                    url + "/" + todoId, HttpMethod.DELETE, null, String.class
            );

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(res.getBody()).isEqualTo("{}");
            assertThatThrownBy(
                    () -> todoService.getTodo(todoId)
            ).isInstanceOfSatisfying(
                    RestException.class,
                    e -> assertThat(e.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }

        @Test
        void failDeleteTodoNotExist() {
            ResponseEntity<Error> res = restTemplate.exchange(
                    url + "/9999", HttpMethod.DELETE, null, Error.class
            );

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(Objects.requireNonNull(res.getBody()).message()).isEqualTo("No such todo: 9999");
            assertThat(res.getBody().errorCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }
    }

    @Nested
    class AddTag {
        @Test
        void addTag() {
            long todoId = todoService.insertTodo(Todo.builder().title("t1").description("d1").build()).getTodoId();
            Tag tag = tagService.insertTag(new Tag().setName("name1"));

            ResponseEntity<String> res = restTemplate.exchange(
                    url + "/" + todoId + "/tags/" + tag.getName(),
                    HttpMethod.PUT,
                    null,
                    String.class
            );

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(res.getBody()).isEqualTo("{}");
            TodoWithTags todoWithTags = todoTagService.getTodoWithTags(todoId);
            assertThat(todoWithTags.getTodoId()).isEqualTo(todoId);
            assertThat(todoWithTags.getTags()).hasSize(1);
            assertThat(todoWithTags.getTags().get(0).getName()).isEqualTo(tag.getName());
        }

        @Test
        void failAddTagTodoNotExist() {
            Tag tag = tagService.insertTag(new Tag().setName("name1"));

            ResponseEntity<Error> res = restTemplate.exchange(
                    url + "/9999/tags/" + tag.getName(), HttpMethod.PUT, null, Error.class);

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(Objects.requireNonNull(res.getBody()).message()).isEqualTo("No such todo: 9999");
            assertThat(res.getBody().errorCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }

        @Test
        void failAddTagConflictTag() {
            long todoId = todoService.insertTodo(Todo.builder().title("t1").description("d1").build()).getTodoId();
            Tag tag = tagService.insertTag(new Tag().setName("name1"));
            todoTagService.addTag(todoId, tag.getName());

            ResponseEntity<Error> res = restTemplate.exchange(
                    url + "/" + todoId + "/tags/" + tag.getName(), HttpMethod.PUT, null, Error.class);


            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(Objects.requireNonNull(res.getBody()).message())
                    .isEqualTo(String.format("Todo: %s already has tag: %s", todoId, tag.getName()));
            assertThat(res.getBody().errorCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        }
    }

    @Nested
    class DeleteTag {
        @Test
        void deleteTag() {
            long todoId = todoService.insertTodo(Todo.builder().title("t1").description("d1").build()).getTodoId();
            Tag tag = tagService.insertTag(new Tag().setName("name1"));
            todoTagService.addTag(todoId, tag.getName());

            ResponseEntity<String> res = restTemplate.exchange(
                    url + "/" + todoId + "/tags/" + tag.getName(),
                    HttpMethod.DELETE,
                    null,
                    String.class
            );

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(res.getBody()).isEqualTo("{}");
            TodoWithTags todoWithTags = todoTagService.getTodoWithTags(todoId);
            assertThat(todoWithTags.getTodoId()).isEqualTo(todoId);
            assertThat(todoWithTags.getTags()).hasSize(0);
        }

        @Test
        void deleteTagTodoNotExist() {
            ResponseEntity<Error> res = restTemplate.exchange(
                    url + "/9999/tags/name1", HttpMethod.DELETE, null, Error.class);

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(Objects.requireNonNull(res.getBody()).message()).isEqualTo("No such todo: 9999");
            assertThat(res.getBody().errorCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }

        @Test
        void deleteTagTagNotExist() {
            long todoId = todoService.insertTodo(Todo.builder().title("t1").description("d1").build()).getTodoId();
            ResponseEntity<Error> res = restTemplate.exchange(
                    url + "/" + todoId + "/tags/name1", HttpMethod.DELETE, null, Error.class);

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(Objects.requireNonNull(res.getBody()).message()).isEqualTo(String.format("Todo: %s doesn't have tag: name1", todoId));
            assertThat(res.getBody().errorCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }
}
