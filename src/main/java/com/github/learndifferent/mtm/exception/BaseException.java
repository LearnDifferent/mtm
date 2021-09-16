package com.github.learndifferent.mtm.exception;

/**
 * 基础 Exception
 *
 * @author zhou
 * @date 2021/09/05
 */
public class BaseException extends RuntimeException {

    public BaseException() {
        super();
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }
}