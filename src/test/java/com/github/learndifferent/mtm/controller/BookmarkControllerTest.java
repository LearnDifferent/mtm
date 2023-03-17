package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.config.mvc.CustomWebConfig;
import com.github.learndifferent.mtm.query.BasicWebDataRequest;
import com.github.learndifferent.mtm.service.impl.BookmarkServiceImpl;
import com.github.learndifferent.mtm.vo.BookmarkVO;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * 下文中的注释来自 Cursor 的 AI
 * 测试类使用了 JUnit 5 和 Spring Boot 的 WebMvcTest 注解
 * 类注解 @WebMvcTest 表示这是一个针对 BookmarkController 类的 Web MVC 测试
 */
@WebMvcTest(BookmarkController.class)
class BookmarkControllerTest {

    // @Autowired 注解用于自动注入 MockMvc 类的实例。
    @Autowired
    // MockMvc 类用于模拟 HTTP 请求，以便在测试中使用。
    private MockMvc mockMvc;

    @MockBean
    private BookmarkServiceImpl bookmarkService;

    @MockBean
    private CustomWebConfig config;

    /**
     * 这个测试方法用于检查当调用 /bookmark 端点时，是否返回了状态码 200。
     *
     * @throws Exception 如果出现异常，则抛出异常。
     */
    @Test
    @DisplayName("Should return result code of 200")
    void shouldReturnResultCodeOf200() throws Exception {

        // 从资源文件中读取请求体数据
        URL resource = BookmarkControllerTest.class.getClassLoader()
                .getResource("request/bookmark/basic-bookmark-data.json");
        assert resource != null;
        Path path = Paths.get(resource.toURI());
        byte[] requestBody = Files.readAllBytes(path);

        // 使用了 Mockito.mockStatic 来模拟 StpUtil.getLoginIdAsString 方法的调用。
        try (MockedStatic<StpUtil> stpUtil = Mockito.mockStatic(StpUtil.class)) {

            stpUtil.when(StpUtil::getLoginIdAsString).thenReturn("any");

            // 使用了 BDDMockito.given 来设置 bookmarkService.addToBookmark 方法的预期行为。
            BDDMockito.given(bookmarkService.addToBookmark(
                            ArgumentMatchers.any(BasicWebDataRequest.class), ArgumentMatchers.anyString()))
                    .willReturn(true);

            // 使用 mockMvc.perform 方法模拟了一个 POST 请求，并检查了响应的状态码和 JSON 路径中的 code 值。
            mockMvc.perform(
                            MockMvcRequestBuilders
                                    // 模拟 Post 请求
                                    .post("/bookmark")
                                    // 设置请求体（Request Body）
                                    .content(requestBody)
                                    // 设置请求体的类型为 JSON
                                    .contentType(MediaType.APPLICATION_JSON)
                    ).andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200));
        }
    }

    @Test
    @DisplayName("Should return the bookmark with the same ID")
    void shouldReturnTheBookmarkWithSameId() throws Exception {
        String currentUsername = "currentUsername";
        int id = 1;
        BookmarkVO bookmark = new BookmarkVO();
        bookmark.setId(id);

        try (MockedStatic<StpUtil> stpUtil = Mockito.mockStatic(StpUtil.class)) {
            stpUtil.when(StpUtil::getLoginIdAsString).thenReturn(currentUsername);
            BDDMockito.given(bookmarkService.getBookmark(id, currentUsername))
                    .willReturn(bookmark);

            // 使用 mockMvc.perform 方法模拟了一个 GET 请求，并检查了响应的状态码和 JSON 路径中的 id 值。
            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .get("/bookmark/get")
                                    .param("id", "1"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers
                            .jsonPath("$.id")
                            .value(id));
        }
    }
}