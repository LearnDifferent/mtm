package com.github.learndifferent.mtm.utils;

/**
 * A utility class for pagination
 *
 * @author zhou
 * @date 2021/09/05
 */
public class PaginationUtils {

    private PaginationUtils() {
    }

    /**
     * Get the {@code from} index
     *
     * @param currentPage current page
     * @param pageSize    page size
     * @return from index
     */
    public static int getFromIndex(int currentPage, int pageSize) {
        return (currentPage - 1) * pageSize;
    }

    /**
     * Get total pages
     *
     * @param totalCount total count
     * @param pageSize   page size
     * @return total pages
     */
    public static int getTotalPages(int totalCount, int pageSize) {
        return totalCount % pageSize == 0 ? totalCount / pageSize
                : totalCount / pageSize + 1;
    }

    /**
     * Ensure current page is always greater than 0
     *
     * @param currentPage current page
     * @return the current page that is always greater than 0
     */
    public static int constrainGreaterThanZero(int currentPage) {
        if (currentPage < 1) {
            currentPage = 1;
        }
        return currentPage;
    }
}