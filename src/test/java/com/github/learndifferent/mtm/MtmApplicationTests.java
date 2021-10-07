package com.github.learndifferent.mtm;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class MtmApplicationTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void test() {
        stringRedisTemplate.delete("testing");
        stringRedisTemplate.opsForList().rightPush("testing", "123");
        stringRedisTemplate.opsForList().rightPush("testing", "fe");
        stringRedisTemplate.opsForList().rightPush("testing", "fefe");
        stringRedisTemplate.opsForList().rightPush("testing", "jifei123");
        List<String> testing = stringRedisTemplate.opsForList().range("testing", 0, 100);
        testing.forEach(System.out::println);
    }
}
