package com.github.learndifferent.mtm.manager;

import static org.mockito.ArgumentMatchers.anyString;
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

@ExtendWith(MockitoExtension.class)
class NotificationManagerTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private SetOperations<String, String> redisSetOperations;

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
}