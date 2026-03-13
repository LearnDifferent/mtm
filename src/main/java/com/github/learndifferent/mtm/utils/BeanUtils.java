package com.github.learndifferent.mtm.utils;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Utility for converting between DO, DTO, and VO objects
 *
 * @author zhou
 * @date 2021/09/05
 */
public class BeanUtils {

    private static final Mapper MAPPER = DozerBeanMapperBuilder.buildDefault();

    private BeanUtils() {
    }

    /**
     * Converts the current object to another type
     *
     * @param sourceObject Object to convert
     * @param desClass     Target class to convert to
     * @param <T>          Type parameter
     * @return New object (returns null if source object is null)
     */
    public static <T> T convert(Object sourceObject, Class<T> desClass) {

        if (sourceObject == null) {
            return null;
        }

        return MAPPER.map(sourceObject, desClass);
    }

    /**
     * Converts an entire collection to a list of another type
     *
     * @param sourceList Source collection
     * @param desClass   Target class
     * @param <T>        Type parameter
     * @return New list
     */
    public static <T> List<T> convertList(Collection<?> sourceList, Class<T> desClass) {
        if (sourceList == null) {
            return Collections.emptyList();
        }
        List<T> desList = new ArrayList<>();
        for (Object sourceObject : sourceList) {
            T desObject = MAPPER.map(sourceObject, desClass);
            desList.add(desObject);
        }
        return desList;
    }

    /**
     * Copies values from object A to object B
     *
     * @param sourceObject Object A
     * @param desObject    Object B
     */
    public static void copy(Object sourceObject, Object desObject) {
        if (sourceObject == null || desObject == null) {
            return;
        }
        MAPPER.map(sourceObject, desObject);
    }
}
