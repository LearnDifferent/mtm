package com.github.learndifferent.mtm.annotation.validation.user.role.admin;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.utils.ReverseUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 检查是否为管理员，如果不是管理员账户，就抛出异常
 *
 * @author zhou
 * @date 2021/09/05
 */
@Aspect
@Component
public class AdminValidationAspect {

    @Pointcut("@annotation(com.github.learndifferent.mtm.annotation.validation.user.role.admin.AdminValidation)")
    public void pointcuts() {
    }

    @Before(value = "pointcuts()")
    public void checkAdmin() {
        if (ReverseUtils.notAdmin()) {
            throw new ServiceException(ResultCode.PERMISSION_DENIED);
        }
    }
}
