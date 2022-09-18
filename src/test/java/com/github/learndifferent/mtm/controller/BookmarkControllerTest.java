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

@WebMvcTest(BookmarkController.class)
class BookmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookmarkServiceImpl bookmarkService;

    @MockBean
    private CustomWebConfig config;

    @Test
    @DisplayName("Should return result code of 200")
    void shouldReturnResultCodeOf200() throws Exception {

        URL resource = BookmarkControllerTest.class.getClassLoader()
                .getResource("request/bookmark/basic-bookmark-data.json");
        assert resource != null;
        Path path = Paths.get(resource.toURI());
        byte[] requestBody = Files.readAllBytes(path);

        try (MockedStatic<StpUtil> stpUtil = Mockito.mockStatic(StpUtil.class)) {

            stpUtil.when(StpUtil::getLoginIdAsString).thenReturn("any");

            BDDMockito.given(bookmarkService.addToBookmark(
                    ArgumentMatchers.any(BasicWebDataRequest.class), ArgumentMatchers.anyString()))
                    .willReturn(true);

            mockMvc.perform(
                    MockMvcRequestBuilders
                            .post("/bookmark")
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers
                            .jsonPath("$.code")
                            .value(200));
        }
    }

    @Test
    @DisplayName("Should return the bookmark with the same ID")
    void shouldReturnTheBookmarkWithSameId() throws Exception {
        String currentUsername = "currentUsername";
        int webId = 1;
        BookmarkVO bookmark = new BookmarkVO();
        bookmark.setWebId(webId);

        try (MockedStatic<StpUtil> stpUtil = Mockito.mockStatic(StpUtil.class)) {
            stpUtil.when(StpUtil::getLoginIdAsString).thenReturn(currentUsername);
            BDDMockito.given(bookmarkService.getBookmark(webId, currentUsername))
                    .willReturn(bookmark);

            mockMvc.perform(
                    MockMvcRequestBuilders
                            .get("/bookmark/get")
                            .param("webId", "1"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers
                            .jsonPath("$.webId")
                            .value(webId));
        }
    }
}