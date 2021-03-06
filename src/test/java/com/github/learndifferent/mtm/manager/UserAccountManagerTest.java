package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.mapper.UserMapper;
import com.github.learndifferent.mtm.utils.Md5Util;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserAccountManagerTest {

    @InjectMocks
    private UserAccountManager userAccountManager;

    @Mock
    private UserMapper userMapper;

    @Test
    @DisplayName("Should get the ID")
    void shouldGetTheId() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String id = "user_id_001";
        String username = "user1";
        String password = "password123";
        String encryptedPassword = Md5Util.getMd5(password);

        Mockito.when(userMapper.getUserIdByNameAndPassword(username, encryptedPassword))
                .thenReturn(id);

        Method method = getCheckUserExistsAndReturnUserIdMethod();
        Object result = method.invoke(userAccountManager, username, password);
        String userId = String.valueOf(result);
        Assertions.assertEquals(id, userId);
    }

    private Method getCheckUserExistsAndReturnUserIdMethod() throws ServiceException, NoSuchMethodException {

        Method method = UserAccountManager.class.getDeclaredMethod(
                "checkUserExistsAndReturnUserId", String.class, String.class);
        method.setAccessible(true);
        return method;
    }
}