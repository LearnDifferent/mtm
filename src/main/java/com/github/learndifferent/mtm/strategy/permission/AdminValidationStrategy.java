package com.github.learndifferent.mtm.strategy.permission;

import com.github.learndifferent.mtm.constant.consist.PermissionCheckConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.utils.LoginUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.lang.annotation.Annotation;
import org.springframework.stereotype.Component;

/**
 * Check if the user is admin
 *
 * @author zhou
 * @date 2023/10/17
 */
@Component(PermissionCheckConstant.IS_ADMIN)
public class AdminValidationStrategy implements PermissionCheckStrategy {

    @Override
    public void checkPermission(Annotation[][] parameterAnnotations, Object[] args) {
        boolean isNotAdmin = LoginUtils.isNotAdmin();
        ThrowExceptionUtils.throwIfTrue(isNotAdmin, ResultCode.PERMISSION_DENIED);
    }
}