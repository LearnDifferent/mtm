package com.github.learndifferent.mtm.response;

import com.github.learndifferent.mtm.constant.enums.ResultCode;

/**
 * 用于生成 ResultVO
 *
 * @author zhou
 * @date 2021/09/05
 */
public class ResultCreator {

    public static <T> ResultVO<T> result(ResultCode resultCode, final T data) {
        return new ResultVO<T>(resultCode.value(), resultCode.msg(), data);
    }

    public static <T> ResultVO<T> result(ResultCode resultCode) {
        return result(resultCode, null);
    }

    public static <T> ResultVO<T> okResult() {
        return result(ResultCode.SUCCESS, null);
    }

    public static <T> ResultVO<T> okResult(final T data) {
        return result(ResultCode.SUCCESS, data);
    }

    public static <T> ResultVO<T> okResult(String msg, final T data) {
        return new ResultVO<T>(ResultCode.SUCCESS.value(), msg, data);
    }

    public static <T> ResultVO<T> failResult() {
        return result(ResultCode.FAILED, null);
    }

    public static <T> ResultVO<T> failResult(final String msg) {
        return failResult(msg, null);
    }

    public static <T> ResultVO<T> failResult(final T data) {
        return result(ResultCode.FAILED, data);
    }

    public static <T> ResultVO<T> defaultFailResult() {
        return new ResultVO<T>(ResultCode.FAILED.value(), "Something went wrong. Please try again later.", null);
    }

    public static <T> ResultVO<T> failResult(String msg, final T data) {
        return new ResultVO<T>(ResultCode.FAILED.value(), msg, data);
    }
}
