package com.github.learndifferent.mtm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.learndifferent.mtm.constant.enums.UserRole;
import com.github.learndifferent.mtm.entity.UserDO;
import com.github.learndifferent.mtm.manager.UserAccountManager;
import com.github.learndifferent.mtm.mapper.UserMapper;
import com.github.learndifferent.mtm.query.UserIdentificationRequest;
import com.github.learndifferent.mtm.vo.UserVO;
import java.time.Instant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserAccountManager userAccountManager;

    @Nested
    @DisplayName("Check user")
    class CheckUser {

        private final String USERNAME = "user1";
        private final String ROLE = UserRole.USER.role();
        private final String USER_ID = "user_id";
        private final String PASSWORD = "123456";
        private final Instant CREATION_TIME = Instant.now();

        private final UserDO USER = new UserDO(USERNAME, USER_ID, PASSWORD, CREATION_TIME, ROLE);

        @Test
        @DisplayName("Should return the username")
        void shouldReturnTheUsername() {
            UserRole userRole = UserRole.USER;

            Mockito.when(userAccountManager.createUserAndGetUsername(USERNAME, PASSWORD, userRole))
                    .thenReturn(USERNAME);

            UserIdentificationRequest request = new UserIdentificationRequest(USERNAME, PASSWORD);
            String name = userService.addUserAndGetUsername(request, userRole);
            Assertions.assertEquals(USERNAME, name);
        }

        @Test
        @DisplayName("Should return same user")
        void shouldReturnSameUser() {

            Mockito.when(userMapper.getUserByName(USERNAME)).thenReturn(USER);

            UserVO u = userService.getUserByName(USERNAME);

            Assertions.assertAll(() -> assertEquals(USER_ID, u.getId()),
                    () -> assertEquals(USERNAME, u.getUserName()),
                    () -> assertEquals(CREATION_TIME, u.getCreateTime()),
                    () -> assertEquals(ROLE, USER.getRole()));
        }


        @Test
        @DisplayName("Should return same role")
        void shouldReturnSameRole() {

            Mockito.when(userMapper.getRoleByName(USERNAME)).thenReturn(ROLE);

            String userRole = userService.getRoleByName(USERNAME);
            Assertions.assertEquals(ROLE, userRole);
        }
    }

}