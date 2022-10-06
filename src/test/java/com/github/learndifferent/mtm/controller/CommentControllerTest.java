package com.github.learndifferent.mtm.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.config.mvc.CustomWebConfig;
import com.github.learndifferent.mtm.service.impl.CommentServiceImpl;
import com.github.learndifferent.mtm.vo.CommentVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentServiceImpl commentService;

    @MockBean
    private CustomWebConfig config;

    @Nested
    class GetCommentById {

        private final int ID = 100;
        private final CommentVO COMMENT = new CommentVO().setId(ID);
        private final Integer WEB_ID = 1;
        private final String USER_NAME = "user1";

        @BeforeEach
        void setUp() {
            given(commentService.getCommentByIds(ID, WEB_ID, USER_NAME)).willReturn(COMMENT);
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