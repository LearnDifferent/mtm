package com.github.learndifferent.mtm.config.mvc;

import com.github.learndifferent.mtm.constant.enums.ConvertByNames;
import com.github.learndifferent.mtm.exception.ServiceException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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

    private final Map<String, E> names;

    public ConvertByNamesConverter(Class<E> type) {
        names = new HashMap<>();
        E[] es = type.getEnumConstants();
        Arrays.stream(es).forEach(e -> {
            String[] nfc = e.namesForConverter();
            Arrays.stream(nfc).forEach(n -> names.put(n, e));
        });
    }

    @Override
    public E convert(String source) {
        E e1 = names.get(source.toLowerCase());
        E e2 = names.get(source.toUpperCase());
        if (e1 == null && e2 == null) {
            throw new ServiceException("Can't convert " + source);
        }
        return e1 != null ? e1 : e2;
    }
}