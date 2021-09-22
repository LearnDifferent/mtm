package com.github.learndifferent.mtm.response;

/**
 * 用于返回给前端结果
 *
 * @author zhou
 * @date 2021/09/05
 */
public class ResultVO<T> {

    /**
     * Result Code
     */
    private Integer code;
    /**
     * Message
     */
    private String msg;
    /**
     * Data
     */
    private T data;

    public ResultVO(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResultVO<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public ResultVO<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public ResultVO<T> setData(T data) {
        this.data = data;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ResultVO{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
