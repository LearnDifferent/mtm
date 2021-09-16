package com.github.learndifferent.mtm.annotation.validation.register;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.RoleType;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.manager.VerificationCodeManager;
import com.github.learndifferent.mtm.utils.ReverseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 注册用户之前，对验证码进行判断。如果是 admin，还需要判断邀请码。
 * 如果验证码或邀请码出错，会抛出自定义的 ServiceException，代码分别为：
 * ResultCode.VERIFICATION_CODE_FAILED 和 ResultCode.INVITATION_CODE_FAILED
 *
 * @author zhou
 * @date 2021/09/05
 */
@Aspect
@Component
@Slf4j
public class RegisterCodeCheckAspect {

    private final VerificationCodeManager codeManager;

    @Autowired
    public RegisterCodeCheckAspect(VerificationCodeManager codeManager) {
        this.codeManager = codeManager;
    }

    @Before("@annotation(annotation)")
    public void check(RegisterCodeCheck annotation) {

        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new ServiceException("No attributes available.");
        }

        HttpServletRequest request = attributes.getRequest();

        // 参数名称
        String codeParamName = annotation.codeParamName();
        String verifyTokenParamName = annotation.verifyTokenParamName();
        String roleParamName = annotation.roleParamName();
        String invitationCodeParamName = annotation.invitationCodeParamName();

        // 获取参数值
        String code = getParamStringValue(request, codeParamName);
        String verifyToken = getParamStringValue(request, verifyTokenParamName);
        String invitationCode = getParamStringValue(request, invitationCodeParamName);
        RoleType role = getParamRoleValue(request, roleParamName);

        checkCodes(code, verifyToken, role, invitationCode);
    }

    @NotNull
    private String getParamStringValue(@NotNull HttpServletRequest request,
                                       String parameterName) {

        String parameterValue = request.getParameter(parameterName);
        return parameterValue == null ? "" : parameterValue;
    }

    private RoleType getParamRoleValue(@NotNull HttpServletRequest request,
                                       String parameterName) {

        String roleString = request.getParameter(parameterName);
        RoleType role = RoleType.USER;

        if (StringUtils.isNotEmpty(roleString)) {
            role = castRoleStringToRoleType(roleString);
        }

        return role;
    }

    private RoleType castRoleStringToRoleType(String role) {

        try {
            // 通过 valueOf 方法直接从大写的字符串中获取相应的 Enum
            return RoleType.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            e.printStackTrace();
            // 找不到的时候，默认返回角色 user
            return RoleType.USER;
        }
    }

    /**
     * 检查验证码和邀请码。出错的话抛出相应的异常
     *
     * @param code           验证码
     * @param verifyToken    token
     * @param role           角色
     * @param invitationCode 邀请码
     * @throws ServiceException ResultCode.VERIFICATION_CODE_FAILED
     *                          和 ResultCode.INVITATION_CODE_FAILED
     */
    private void checkCodes(String code,
                            String verifyToken,
                            RoleType role,
                            String invitationCode) {

        // 如果验证码错误，抛出自定义异常
        codeManager.checkCode(verifyToken, code);

        if (RoleType.ADMIN == role) {
            // 如果角色是 Admin，需要确认邀请码，如果邀请码出错，会抛出异常
            checkInvitationCode(invitationCode);
        }
    }

    /**
     * 检查邀请码是否正确，如果不正确，抛出异常。
     *
     * @param invitationCode 邀请码
     * @throws ServiceException ResultCode.INVITATION_CODE_FAILED
     */
    private void checkInvitationCode(String invitationCode) {

        // 邀请码这里设置为固定的 1234，如果邀请码不为 1234（包括 null）说明出错了
        String code = "1234";
        if (ReverseUtils.stringNotEqualsIgnoreCase(code, invitationCode)) {
            // 如果邀请码错误，抛出自定义的异常
            throw new ServiceException(ResultCode.INVITATION_CODE_FAILED);
        }
    }
}
