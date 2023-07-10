package com.github.learndifferent.mtm.exception;

import com.github.learndifferent.mtm.constant.enums.ResultCode;

/**
 * Idempotency Exception
 *
 * @author zhou
 * @date 2023/7/10
 */
public class IdempotencyException extends BaseException {

    private final ResultCode resultCode;
    private final String idempotencyKey;

    public IdempotencyException(final ResultCode resultCode, final String idempotencyKey) {
        super(resultCode.msg());
        this.resultCode = resultCode;
        this.idempotencyKey = idempotencyKey;
    }

    public ResultCode getResultCode() {
        return this.resultCode;
    }

    public String getIdempotencyKey() {
        return this.idempotencyKey;
    }
}
