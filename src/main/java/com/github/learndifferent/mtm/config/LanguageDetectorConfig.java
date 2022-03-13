package com.github.learndifferent.mtm.config;

import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Language Detector Configuration
 *
 * @author zhou
 * @date 2022/3/13
 */
@Configuration
public class LanguageDetectorConfig {

    @Bean
    public LanguageDetector languageDetector() {
        return LanguageDetectorBuilder
                .fromLanguages(Language.JAPANESE, Language.CHINESE)
                .build();
    }
}
