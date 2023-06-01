package com.github.learndifferent.mtm.config.mvc;

import com.github.learndifferent.mtm.constant.enums.ConvertByNames;
import com.github.learndifferent.mtm.exception.ServiceException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * Convert a string value to an Enum according to the names
 *
 * @author zhou
 * @date 2022/4/9
 */
public class ConvertByNamesConverterFactory implements ConverterFactory<String, ConvertByNames> {

    @Override
    public <T extends ConvertByNames> Converter<String, T> getConverter(Class<T> targetType) {
        return new ConvertByNamesConverter<>(targetType);
    }
}

class ConvertByNamesConverter<E extends ConvertByNames> implements Converter<String, E> {

    private final Map<String, E> namesEnumMap;

    public ConvertByNamesConverter(Class<E> type) {
        namesEnumMap = new HashMap<>();
        E[] es = type.getEnumConstants();
        Arrays.stream(es).forEach(e -> {
            String[] names = e.namesForConverter();
            Arrays.stream(names).forEach(name -> namesEnumMap.put(name, e));
        });
    }

    @Override
    public E convert(String source) {
        E e1 = namesEnumMap.get(source.toLowerCase());
        E e2 = namesEnumMap.get(source.toUpperCase());
        E e3 = namesEnumMap.get(source);

        if (Objects.nonNull(e1)) {
            return e1;
        }

        if (Objects.nonNull(e2)) {
            return e2;
        }

        if (Objects.nonNull(e3)) {
            return e3;
        }

        throw new ServiceException("Can't convert " + source);
    }
}