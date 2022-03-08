package com.github.learndifferent.mtm.annotation.common;

/**
 * A helper class for annotation
 *
 * @author zhou
 * @date 2022/3/7
 */
public class AnnotationHelper {

    /**
     * {@code hasAnnotation[i]} will be true if the {@code i}th annotation is found
     */
    private final boolean[] hasAnnotation;
    /**
     * Count how many annotations has not been found yet
     */
    private int count;

    public AnnotationHelper(int totalAnnotations) {
        hasAnnotation = new boolean[totalAnnotations];
        count = hasAnnotation.length;
    }

    /**
     * The {@code i}th annotation is found
     *
     * @param annotationIndex the index of the {@code i}th annotation
     */
    public void findIndex(int annotationIndex) {
        if (!hasAnnotation[annotationIndex]) {
            hasAnnotation[annotationIndex] = true;
            count--;
        }
    }

    /**
     * True if the {@code i}th annotation is not found
     *
     * @param annotationIndex the index of the {@code i}th annotation
     * @return true if the {@code i}th annotation is not found
     */
    public boolean hasNotFoundIndex(int annotationIndex) {
        return !hasAnnotation[annotationIndex];
    }

    /**
     * Check if all annotations are found
     *
     * @return true if all annotations are found
     */
    public boolean hasFoundAll() {
        return count == 0;
    }
}
