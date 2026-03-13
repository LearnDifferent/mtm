package com.github.learndifferent.mtm.utils;

import java.util.Arrays;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class EnvCheckUtil {

    private final Environment environment;

    private static boolean hasTestEnv;

    @PostConstruct
    private void init() {
        String[] activeProfiles = environment.getActiveProfiles();
        hasTestEnv = Arrays.asList(activeProfiles).contains("test");
        log.info("Contain test environment: {}", hasTestEnv);
    }

    public static boolean containTestEnv() {
        return hasTestEnv;
    }
}
