package com.github.learndifferent.mtm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application class
 *
 * @author zhou
 * @date 2021/09/05
 */
@SpringBootApplication
@EnableScheduling
public class MtmApplication {

    public static void main(String[] args) {
        SpringApplication.run(MtmApplication.class, args);
    }

}