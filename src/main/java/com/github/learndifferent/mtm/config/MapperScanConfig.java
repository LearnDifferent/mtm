package com.github.learndifferent.mtm.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * Mapper Scan
 *
 * @author zhou
 * @date 2022/4/14
 */
@MapperScan("com.github.learndifferent.mtm.mapper")
@Configuration
public class MapperScanConfig {}