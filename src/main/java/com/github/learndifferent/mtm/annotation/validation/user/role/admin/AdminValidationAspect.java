package com.github.learndifferent.mtm.annotation.validation.user.role.admin;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.RoleType;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Verify whether the user is admin or not
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
        boolean notAdmin = !StpUtil.hasRole(RoleType.ADMIN.role());
        ThrowExceptionUtils.throwIfTrue(notAdmin, ResultCode.PERMISSION_DENIED);
    }
}
