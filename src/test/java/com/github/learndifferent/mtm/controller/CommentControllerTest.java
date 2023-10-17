package com.github.learndifferent.mtm.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.learndifferent.mtm.config.mvc.CustomWebConfig;
import com.github.learndifferent.mtm.service.impl.CommentServiceImpl;
import com.github.learndifferent.mtm.utils.LoginUtils;
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


/**
 * 下文中的注释来自 Cursor 的 AI。
 * WebMvcTest 这个注解表示这是一个针对 BookmarkController 类的 Web MVC 测试。
 */
@WebMvcTest(CommentController.class)
class CommentControllerTest {

    // 使用 @Autowired 注解自动注入 MockMvc 类的实例。
    @Autowired
    // MockMvc 类用于模拟 HTTP 请求，以便在测试中使用。
    private MockMvc mockMvc;

    // 使用 @MockBean 注解创建 BookmarkServiceImpl 类的 mock 对象。
    @MockBean
    // 这个 mock 对象将在测试中替代实际的服务类。
    private CommentServiceImpl commentService;

    // 这个 mock 对象将在测试中替代实际的配置类。
    @MockBean
    private CustomWebConfig config;

    @Nested
    class GetCommentById {

        /**
         * Comment ID
         */
        private final int ID = 100;
        private final CommentVO COMMENT = new CommentVO().setId(ID);
        private final long BOOKMARK_ID = 1L;
        private final long USER_NAME = 1L;

        @BeforeEach
        void setUp() {
            // 使用 BDDMockito.given 方法设置 mock 对象的预期行为。
            // willReturn 方法表示当调用指定的方法时，应返回的值。
            given(commentService.getCommentByIds(ID, BOOKMARK_ID, USER_NAME))
                    .willReturn(COMMENT);
        }

        @Test
        @DisplayName("Should return the comment")
        void shouldReturnTheComment() throws Exception {

            // 使用 Mockito.mockStatic 方法创建 StpUtil 类的静态方法的 mock 对象。
            // 这个 mock 对象将在测试中替代实际的静态方法。
            try (MockedStatic<LoginUtils> loginUtilsMockedStatic = mockStatic(LoginUtils.class)) {
                // 使用 when 方法设置静态方法的预期行为。thenReturn 方法表示当调用指定的静态方法时，应返回的值。
                loginUtilsMockedStatic.when(LoginUtils::getCurrentUsername).thenReturn(USER_NAME);
                // 使用 mockMvc.perform 方法模拟 HTTP 请求
                mockMvc.perform(
                                // 接受一个 MockHttpServletRequestBuilder 对象，用于构建 HTTP 请求。
                                // 例如，MockMvcRequestBuilders.post("/bookmark") 创建一个 POST 请求，
                                // MockMvcRequestBuilders.get("/bookmark/get") 创建一个 GET 请求。
                                get("/comment")
                                        .param("id", String.valueOf(ID))
                                        .param("bookmarkId", String.valueOf(BOOKMARK_ID)))
                        // 使用 andExpect 方法检查响应的状态码
                        // isXxx() 方法表示预期的状态码
                        // 例如 isOk() 表示预期状态码为 200，isBadRequest() 表示预期状态码为 400。
                        .andExpect(status().isOk())
                        // 使用 andExpect 方法检查 JSON 响应中的特定键值
                        // jsonPath("$.key") 表示要检查的 JSON 路径，value(expectedValue) 表示预期的值。
                        .andExpect(jsonPath("$.code").value(200))
                        .andExpect(jsonPath("$.data.id").value(ID));
            }
        }

        @Test
        @DisplayName("Should return result code of 500")
        void shouldReturnResultCodeOf500() throws Exception {
            String emptyId = "0";
            try (MockedStatic<LoginUtils> loginUtilsMockedStatic = mockStatic(LoginUtils.class)) {
                loginUtilsMockedStatic.when(LoginUtils::getCurrentUsername).thenReturn(USER_NAME);
                mockMvc.perform(
                                get("/comment")
                                        .param("id", emptyId)
                                        .param("bookmarkId", String.valueOf(BOOKMARK_ID)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value(500));
            }
        }
    }

}