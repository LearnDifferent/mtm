package com.github.learndifferent.mtm.exception;

import cn.dev33.satoken.exception.NotLoginException;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局处理异常
 *
 * @author zhou
 * @date 2021/09/05
 */
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 处理自定义的服务型异常
     *
     * @param e 自定义的服务型异常
     * @return 500 错误，包装为自定义的结果
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServiceException.class)
    public ResultVO<?> handleServiceException(final ServiceException e) {

        e.printStackTrace();
        return ResultCreator.result(e.getResultCode());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NotLoginException.class)
    public ResultVO<?> handleNotLoginException(final NotLoginException e) {
        e.printStackTrace();
        return ResultCreator.result(ResultCode.NOT_LOGIN);
    }
}
