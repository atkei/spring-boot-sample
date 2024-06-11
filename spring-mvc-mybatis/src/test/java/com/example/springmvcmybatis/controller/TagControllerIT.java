package com.example.springmvcmybatis.controller;

import com.example.springmvcmybatis.dto.Error;
import com.example.springmvcmybatis.dto.Tag;
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


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TagControllerIT {
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
        url = "http://localhost:" + port + "/v1/tags";
    }

    @BeforeEach
    void setup() {
        todoService.deleteAllTodos();
        tagService.deleteAllTags();
    }

    @Nested
    class CreateTag {
        @Test
        void createTag() {
            Tag req = new Tag().setName("name1");
            Tag res = restTemplate.postForObject(url, req, Tag.class);

            assertThat(res.getName()).isEqualTo(req.getName());
        }

        @Test
        void failCreateTagAlreadyExist() {
            Tag req = new Tag().setName("name1");
            tagService.insertTag(req);
            ResponseEntity<Error> res = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(req), Error.class);

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(Objects.requireNonNull(res.getBody()).message()).isEqualTo("Tag: name1 already exists");
            assertThat(res.getBody().errorCode()).isEqualTo(HttpStatus.CONFLICT.value());
        }
    }

    @Nested
    class GetTag {
        static Stream<Arguments> getTagsParamsProvider() {
            return Stream.of(
                    // limit, offset, expectedCount, expectedHeadIdx, expectedTailIdx
                    Arguments.arguments(100, 0, 4, 0, 3),
                    Arguments.arguments(2, 1, 2, 1, 2),
                    Arguments.arguments(1, 3, 1, 3, 3)
            );
        }

        @ParameterizedTest
        @MethodSource("getTagsParamsProvider")
        void getTags(
                final int limit,
                final int offset,
                final int expectedCount,
                final int expectedHeadIdx,
                final int expectedTailIdx) {
            List<Tag> tags = new ArrayList<>();
            tags.add(tagService.insertTag(new Tag().setName("name1")));
            tags.add(tagService.insertTag(new Tag().setName("name2")));
            tags.add(tagService.insertTag(new Tag().setName("name3")));
            tags.add(tagService.insertTag(new Tag().setName("name4")));

            ResponseEntity<List<Tag>> res = restTemplate.exchange(
                    url + "?limit=" + limit + "&offset=" + offset,
                    HttpMethod.GET, null, new ParameterizedTypeReference<>() {}
            );

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
            List<Tag> body = Objects.requireNonNull(res.getBody());
            assertThat(body).hasSize(expectedCount);
            if (expectedCount > 0) {
                assertThat(body.get(0)).isEqualTo(tags.get(expectedHeadIdx));
                assertThat(body.get(expectedCount - 1)).isEqualTo(tags.get(expectedTailIdx));
            }
        }

        @Test
        void getTag() {
            Tag tag = tagService.insertTag(new Tag().setName("name1"));

            ResponseEntity<Tag> res = restTemplate.exchange(
                    url + "/" + tag.getName(), HttpMethod.GET, null, Tag.class
            );

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(res.getBody()).isEqualTo(tag);
        }
    }

    @Nested
    class DeleteTag {
        @Test
        void deleteTag() {
            Tag tag = tagService.insertTag(new Tag().setName("name1"));

            ResponseEntity<String> res = restTemplate.exchange(
                    url + "/" + tag.getName(), HttpMethod.DELETE, null, String.class
            );

            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(res.getBody()).isEqualTo("{}");
        }
    }
}
