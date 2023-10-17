package com.github.learndifferent.mtm.strategy.permission;

import com.github.learndifferent.mtm.constant.consist.PermissionCheckConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.utils.LoginUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.lang.annotation.Annotation;
import org.springframework.stereotype.Component;

/**
 * Check if the user is not a guest
 *
 * @author zhou
 * @date 2023/10/17
 */
@Component(PermissionCheckConstant.IS_NOT_GUEST)
public class NotGuestValidationStrategy implements PermissionCheckStrategy {

    @Override
    public void checkPermission(Annotation[][] parameterAnnotations, Object[] args) {
        boolean isGuest = LoginUtils.isGuest();
        ThrowExceptionUtils.throwIfTrue(isGuest, ResultCode.PERMISSION_DENIED);
    }
}
