package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.entity.UserDO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.mapper.UserMapper;
import com.github.learndifferent.mtm.utils.PasswordUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserManagerTest {

    @InjectMocks
    private UserManager userManager;

    @Mock
    private UserMapper userMapper;

    @Test
    @DisplayName("Should get the ID")
    void shouldGetTheId() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Long id = 1L;
        String username = "user1";
        String password = "password123";
        String encryptedPassword = PasswordUtils.encode(password);
        UserDO user = new UserDO(id, username, encryptedPassword, Instant.now(), "user");

        Mockito.when(userMapper.getUserByName(username)).thenReturn(user);

        Method method = getCheckUserExistsAndReturnUserIdMethod();
        Object result = method.invoke(userManager, username, password);
        Long userId = (Long) result;
        Assertions.assertEquals(id, userId);
    }

    private Method getCheckUserExistsAndReturnUserIdMethod() throws ServiceException, NoSuchMethodException {

        Method method = UserManager.class.getDeclaredMethod(
                "checkUserExistsAndReturnUserId", String.class, String.class);
        method.setAccessible(true);
        return method;
    }
}
