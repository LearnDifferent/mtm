package com.github.learndifferent.mtm.annotation.validation.user.delete;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.common.Password;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.RoleType;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.utils.ReverseUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 删除用户前查看是否可以删除
 *
 * @author zhou
 * @date 2021/09/05
 */
@Aspect
@Component
public class DeleteUserCheckAspect {

    private final UserService userService;

    @Autowired
    public DeleteUserCheckAspect(UserService userService) {
        this.userService = userService;
    }

    @Before("@annotation(deleteUserCheck)")
    public void check(JoinPoint jointPoint, DeleteUserCheck deleteUserCheck) {

        HttpServletRequest request = getRequest();

        MethodSignature signature = (MethodSignature) jointPoint.getSignature();
        Method method = signature.getMethod();

        String[] parameterNames = signature.getParameterNames();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        String username = "";
        String password = "";

        int counter = 0;
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof Username) {
                    username = getValueFromRequest(request, parameterNames[i]);
                    counter++;
                    break;
                }
                if (annotation instanceof Password) {
                    password = getValueFromRequest(request, parameterNames[i]);
                    counter++;
                    break;
                }
            }
            if (counter == 2) {
                break;
            }
        }

        checkUserExists(username, password);
        checkDeletePermission(username);
    }

    @NotNull
    private HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (requestAttributes == null) {
            throw new ServiceException("No Request Attributes Available.");
        }

        return requestAttributes.getRequest();
    }

    private String getValueFromRequest(HttpServletRequest request,
                                       String parameterName) {
        return request.getParameter(parameterName);
    }

    /**
     * 检查用户是否存在
     *
     * @param username 用户名
     * @param password 密码
     * @throws ServiceException 用户不存在异常 ResultCode.USER_NOT_EXIST
     */
    private void checkUserExists(String username, String password) {
        UserDTO user = userService.getUserByNameAndPwd(username, password);
        if (user == null) {
            throw new ServiceException(ResultCode.USER_NOT_EXIST);
        }
    }

    /**
     * 检查删除用户的权限：只有该用户有删除该用户的权限，且 guest 用户无法被删除
     *
     * @param userName 用户名
     * @throws ServiceException 没有权限异常：ResultCode.PERMISSION_DENIED
     */
    private void checkDeletePermission(String userName) {

        String currentUsername = (String) StpUtil.getLoginId();

        if (StpUtil.hasRole(RoleType.GUEST.role())
                || ReverseUtils.stringNotEqualsIgnoreCase(currentUsername, userName)) {
            // 如果不是当前用户删除自己的帐号，就抛出异常；如果删除的是 Guest 用户，也抛出异常
            throw new ServiceException(ResultCode.PERMISSION_DENIED);
        }
    }
}
