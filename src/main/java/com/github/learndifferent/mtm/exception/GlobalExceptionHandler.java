package com.github.learndifferent.mtm.exception;

import cn.dev33.satoken.exception.NotLoginException;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理添加到实体类上的校验参数注解抛出的异常
     *
     * @param e {@link MethodArgumentNotValidException}
     * @return {@link ResultVO}
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVO<?> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {

        log.error("MethodArgumentNotValidException", e);

        // Get error list if there is any field errors
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        // Get all error messages
        List<String> errorMessages = fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        return ResultCreator.result(ResultCode.VALIDATION_FAILED, errorMessages);
    }

    /**
     * 处理直接添加到入参的校验参数注解抛出的异常
     *
     * @param e {@link ConstraintViolationException}
     * @return {@link ResultVO}
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResultVO<?> handleConstraintViolationException(final ConstraintViolationException e) {

        log.error("ConstraintViolationException", e);

        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();

        List<String> messages = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        return ResultCreator.result(ResultCode.VALIDATION_FAILED, messages);
    }

    /**
     * 处理自定义的服务型异常
     *
     * @param e 自定义的服务型异常
     * @return 500 错误，包装为自定义的结果
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServiceException.class)
    public ResultVO<?> handleServiceException(final ServiceException e) {

        log.error("Service Exception", e);

        ResultCode resultCode = e.getResultCode();
        Object data = e.getData();

        return ResultCreator.result(resultCode, data);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NotLoginException.class)
    public ResultVO<?> handleNotLoginException(final NotLoginException e) {

        log.error("Not Login Exception", e);

        return ResultCreator.result(ResultCode.NOT_LOGIN);
    }
}
