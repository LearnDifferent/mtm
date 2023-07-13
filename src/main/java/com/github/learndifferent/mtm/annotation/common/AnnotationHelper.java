package com.github.learndifferent.mtm.annotation.common;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper class to keep track of found annotations.
 *
 * @author zhou
 * @date 2023/7/12
 */
@Slf4j
public class AnnotationHelper {

    private final Set<Class<? extends Annotation>> requiredAnnotations;

    private final Set<Class<? extends Annotation>> foundAnnotations;

    @SafeVarargs
    public AnnotationHelper(Class<? extends Annotation>... requiredAnnotations) {
        this.requiredAnnotations = Arrays.stream(requiredAnnotations).collect(Collectors.toSet());
        this.foundAnnotations = new HashSet<>();
    }

    /**
     * Add an annotation class to the found set if it is required.
     *
     * @param annotation The annotation class to find
     */
    public void findAnnotation(Class<? extends Annotation> annotation) {
        if (requiredAnnotations.contains(annotation)) {
            foundAnnotations.add(annotation);
        } else {
            log.warn("Annotation not required: " + annotation.getSimpleName());
        }
    }

    /**
     * Check if an annotation has not been found yet
     *
     * @param annotation The annotation class to check
     * @return boolean True if the annotation has not been found yet,
     * false if it has already been found
     */
    public boolean hasNotFoundAnnotation(Class<? extends Annotation> annotation) {
        if (requiredAnnotations.contains(annotation)) {
            // Check if the annotation has been found in the found set
            // Return true if not found yet
            return !foundAnnotations.contains(annotation);
        } else {
            log.warn("Annotation not required: " + annotation.getSimpleName());
            return true;
        }
    }

    /**
     * Check if all required annotations have been found.
     *
     * @return true if all required annotations have been found
     */
    public boolean hasFoundAllRequiredAnnotations() {
        return foundAnnotations.containsAll(requiredAnnotations);
    }

}