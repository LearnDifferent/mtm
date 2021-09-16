package com.github.learndifferent.mtm.utils;

/**
 * 分页相关计算
 *
 * @author zhou
 * @date 2021/09/05
 */
public class PageUtil {

    /**
     * 获取起始 Index（也就是 from 参数）
     *
     * @param currentPage 当前在第几页
     * @param pageSize    页面大小
     * @return 起始的 Index
     */
    public static int getFromIndex(int currentPage, int pageSize) {
        return (currentPage - 1) * pageSize;
    }

    /**
     * 计算总共有多少页
     *
     * @param totalCount 总记录数
     * @param pageSize   页面大小
     * @return 总页数
     */
    public static int getAllPages(int totalCount, int pageSize) {
        return totalCount % pageSize == 0
                ? totalCount / pageSize : totalCount / pageSize + 1;
    }

    /**
     * 让数值（一般用于页数）大于 0，也就是从 1 开始
     *
     * @param currentPage 数值（页数）
     * @return 如果小于
     */
    public static int constrainGreaterThanZero(int currentPage) {
        if (currentPage < 1) {
            currentPage = 1;
        }
        return currentPage;
    }
}