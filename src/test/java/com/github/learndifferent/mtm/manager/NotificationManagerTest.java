package com.github.learndifferent.mtm.manager;

import static com.github.learndifferent.mtm.constant.consist.RedisConstant.MUTE_NOTIFICATIONS;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 下文中的注释来自 Cursor 的 AI 和 Codeium。
 * 下面的 @ExtendWith(MockitoExtension.class) 注解表示使用 Mockito 框架进行测试。
 */
@ExtendWith(MockitoExtension.class)
class NotificationManagerTest {

    // 创建Mock对象
    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private SetOperations<String, String> redisSetOperations;

    // 将 Mock 对象注入到当前类的对象中
    @InjectMocks
    private NotificationManager notificationManager;

    @Test
    @DisplayName("Should return false to indicate that the user has turned on notifications")
    void shouldReturnFalseToIndicateThatTheUserHasTurnedOnNotifications() {
        String username = "User0";
        when(redisTemplate.opsForSet()).thenReturn(redisSetOperations);
        when(redisSetOperations.isMember(anyString(), anyString())).thenReturn(false);

        boolean result = notificationManager.checkIfTurnOffNotifications(username);
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("Should turn off notifications")
    void shouldTurnOffNotifications() {
        String username = "user1";

        when(redisTemplate.opsForSet()).thenReturn(redisSetOperations);
        when(notificationManager.checkIfTurnOffNotifications(username)).thenReturn(false);

        notificationManager.turnOnTurnOffNotifications(username);

        // 使用 verify() 方法，验证预期方法被调用的次数。
        verify(redisSetOperations, times(0)).remove(MUTE_NOTIFICATIONS, username);
        verify(redisSetOperations, times(1)).add(MUTE_NOTIFICATIONS, username);
    }

}