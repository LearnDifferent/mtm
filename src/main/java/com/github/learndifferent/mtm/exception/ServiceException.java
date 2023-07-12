package com.github.learndifferent.mtm.exception;

import com.github.learndifferent.mtm.constant.enums.ResultCode;

/**
 * A custom exception class that represents a service error
 *
 * @author zhou
 * @date 2021/09/05
 */
public class ServiceException extends BaseException {

    private final ResultCode resultCode;
    private final Object data;

    public ServiceException(final Throwable e) {
        super(e);
        this.resultCode = ResultCode.FAILED;
        this.data = null;
    }

    public ServiceException(final String message) {
        super(message);
        this.resultCode = ResultCode.FAILED;
        this.data = null;
    }

    public ServiceException(final String message, final Throwable cause) {
        super(message, cause);
        this.resultCode = ResultCode.FAILED;
        this.data = null;
    }

    public ServiceException(final ResultCode resultCode,
                            final String message,
                            final Object data) {
        super(message);
        this.resultCode = resultCode;
        this.data = data;
    }

    public ServiceException(final ResultCode resultCode, final String message) {
        super(message);
        this.resultCode = resultCode;
        this.data = null;
    }

    public ServiceException(final ResultCode resultCode) {
        super(resultCode.msg());
        this.resultCode = resultCode;
        this.data = null;
    }

    public ResultCode getResultCode() {
        return this.resultCode;
    }

    public Object getData() {
        return this.data;
    }
}
