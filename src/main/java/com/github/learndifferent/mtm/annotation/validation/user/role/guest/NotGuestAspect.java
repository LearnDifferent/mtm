package com.github.learndifferent.mtm.annotation.validation.user.role.guest;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.RoleType;
import com.github.learndifferent.mtm.exception.ServiceException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 如果用户角色是 guest，就抛出异常
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
        if (StpUtil.hasRole(RoleType.GUEST.role())) {
            throw new ServiceException(ResultCode.PERMISSION_DENIED);
        }
    }
}
