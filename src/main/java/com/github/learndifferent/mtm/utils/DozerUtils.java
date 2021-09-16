package com.github.learndifferent.mtm.utils;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 转换 DO、DTO 和 VO 等
 *
 * @author zhou
 * @date 2021/09/05
 */
public class DozerUtils {

    private static final Mapper MAPPER = DozerBeanMapperBuilder.buildDefault();

    /**
     * 转换当前对象为另一个类型的对象
     *
     * @param sourceObject 需要转换的对象
     * @param desClass     转换到这个类
     * @param <T>          类型
     * @return 新对象（如果需要转换的对象为 null，就返回 null）
     */
    public static <T> T convert(Object sourceObject, Class<T> desClass) {

        if (sourceObject == null) {
            return null;
        }

        return MAPPER.map(sourceObject, desClass);
    }

    /**
     * 转换整个列表
     *
     * @param sourceList 原列表
     * @param desClass   目标类
     * @param <T>        类型
     * @return 新列表
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
     * 将对象A的值拷贝到对象B中
     *
     * @param sourceObject 对象A
     * @param desObject    对象B
     */
    public static void copy(Object sourceObject, Object desObject) {
        if (sourceObject == null || desObject == null) {
            return;
        }
        MAPPER.map(sourceObject, desObject);
    }
}
