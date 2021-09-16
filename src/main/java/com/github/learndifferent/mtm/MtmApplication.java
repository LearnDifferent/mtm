package com.github.learndifferent.mtm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动入口
 *
 * @author zhou
 * @date 2021/09/05
 */
@MapperScan("com.github.learndifferent.mtm.mapper")
@SpringBootApplication
public class MtmApplication {

    public static void main(String[] args) {
        SpringApplication.run(MtmApplication.class, args);
    }

}
