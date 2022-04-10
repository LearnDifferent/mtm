package com.github.learndifferent.mtm.annotation.validation.user.role.guest;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.UserRole;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Verify whether the user is guest or not
 *
 * @author zhou
 * @date 2021/09/05
 */
@Aspect
@Component
public class NotGuestAspect {

    @Pointcut("@annotation(com.github.learndifferent.mtm.annotation.validation.user.role.guest.NotGuest)")
    public void pointcuts() {
    }

    @Before(value = "pointcuts()")
    public void check() {
        boolean isGuest = StpUtil.hasRole(UserRole.GUEST.role());
        ThrowExceptionUtils.throwIfTrue(isGuest, ResultCode.PERMISSION_DENIED);
    }
}