package com.github.learndifferent.mtm.constant.enums;

/**
 * Convert a string value to an Enum according to the names
 *
 * @author zhou
 * @date 2022/4/9
 */
public interface ConvertByNames {

    /**
     * Names for the converter to convert a string value to an Enum according to the names
     *
     * @return names for the converter
     */
    String[] namesForConverter();
}