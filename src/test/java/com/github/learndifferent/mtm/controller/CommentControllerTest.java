package com.github.learndifferent.mtm.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.config.mvc.CustomWebConfig;
import com.github.learndifferent.mtm.query.CommentHistoryRequest;
import com.github.learndifferent.mtm.service.impl.CommentServiceImpl;
import com.github.learndifferent.mtm.vo.CommentHistoryVO;
import com.github.learndifferent.mtm.vo.CommentVO;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentServiceImpl commentService;

    @MockBean
    private CustomWebConfig config;

    @Test
    @DisplayName("Should return a list of CommentHistoryVO")
    void shouldReturnAListOfCommentHistoryVo() throws Exception {

        CommentHistoryVO first = new CommentHistoryVO();
        CommentHistoryVO second = new CommentHistoryVO();
        String comment0 = "abc";
        String comment1 = "xyz";
        first.setComment(comment0);
        second.setComment(comment1);
        List<CommentHistoryVO> result = Arrays.asList(first, second);

        URL resource = CommentControllerTest.class.getClassLoader()
                .getResource("request/comment/comment-history.json");
        assert resource != null;
        byte[] requestBody = Files.readAllBytes(Paths.get(resource.toURI()));

        try (MockedStatic<StpUtil> stpUtil = mockStatic(StpUtil.class)) {
            stpUtil.when(StpUtil::getLoginIdAsString).thenReturn("any");

            given(commentService.getHistory(anyString(), any(CommentHistoryRequest.class)))
                    .willReturn(result);

            mockMvc.perform(post("/comment/history")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(jsonPath("$.[0].comment").value(comment0))
                    .andExpect(jsonPath("$.[1].comment").value(comment1));
        }
    }

    @Nested
    class GetCommentById {

        private final int ID = 100;
        private final CommentVO COMMENT = new CommentVO().setCommentId(ID);
        private final Integer WEB_ID = 1;
        private final String USER_NAME = "user1";

        @BeforeEach
        void setUp() {
            given(commentService.getCommentById(ID, WEB_ID, USER_NAME)).willReturn(COMMENT);
        }

        @Test
        @DisplayName("Should return the comment")
        void shouldReturnTheComment() throws Exception {

            try (MockedStatic<StpUtil> stpUtil = mockStatic(StpUtil.class)) {
                stpUtil.when(StpUtil::getLoginIdAsString).thenReturn(USER_NAME);
                mockMvc.perform(
                        get("/comment")
                                .param("commentId", String.valueOf(ID))
                                .param("webId", String.valueOf(WEB_ID)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value(200))
                        .andExpect(jsonPath("$.data.commentId").value(ID));
            }
        }

        @Test
        @DisplayName("Should return result code of 500")
        void shouldReturnResultCodeOf500() throws Exception {
            String emptyId = "0";
            try (MockedStatic<StpUtil> stpUtil = mockStatic(StpUtil.class)) {
                stpUtil.when(StpUtil::getLoginIdAsString).thenReturn(USER_NAME);
                mockMvc.perform(
                        get("/comment")
                                .param("commentId", emptyId)
                                .param("webId", String.valueOf(WEB_ID)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value(500));
            }
        }
    }

}