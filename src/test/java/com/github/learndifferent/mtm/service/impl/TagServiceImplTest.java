package com.github.learndifferent.mtm.service.impl;


import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.mapper.TagMapper;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @InjectMocks
    private TagServiceImpl tagService;

    @Mock
    private TagMapper tagMapper;

    @Test
    @DisplayName("Should throw an exception")
    void shouldThrowAnException() {
        String tagName = "tag1";
        int from = 0;
        int size = 1;
        PageInfoDTO pageInfo = PageInfoDTO.builder().from(from).size(size).build();

        // return empty list
        Mockito.when(tagMapper.getWebIdByTagName(tagName, from, size))
                .thenReturn(new ArrayList<>());

        Assertions.assertThrows(ServiceException.class,
                () -> tagService.getBookmarksByUsernameAndTag("", tagName, pageInfo));
    }

}