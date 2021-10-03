package com.github.learndifferent.mtm.annotation.validation.register;

import com.github.learndifferent.mtm.annotation.common.InvitationCode;
import com.github.learndifferent.mtm.annotation.common.InvitationCodeToken;
import com.github.learndifferent.mtm.annotation.common.UserRole;
import com.github.learndifferent.mtm.annotation.common.VerificationCode;
import com.github.learndifferent.mtm.annotation.common.VerificationCodeToken;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.RoleType;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.service.InvitationCodeService;
import com.github.learndifferent.mtm.service.VerificationCodeService;
import com.github.learndifferent.mtm.utils.ReverseUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 注册用户之前，对验证码进行判断。如果是 admin，还需要判断邀请码。
 * <p>如果验证码或邀请码出错，会抛出自定义的 ServiceException，代码分别为：
 * ResultCode.VERIFICATION_CODE_FAILED 和 ResultCode.INVITATION_CODE_FAILED</p>
 * <p>如果没有传入角色的参数，状态码为：USER_ROLE_NOT_FOUND</p>
 *
 * @author zhou
 * @date 2021/09/05
 */
@Aspect
@Component
@Slf4j
public class RegisterCodeCheckAspect {

    private final VerificationCodeService verificationCodeService;
    private final InvitationCodeService invitationCodeService;

    @Autowired
    public RegisterCodeCheckAspect(VerificationCodeService verificationCodeService,
                                   InvitationCodeService invitationCodeService) {
        this.verificationCodeService = verificationCodeService;
        this.invitationCodeService = invitationCodeService;
    }

    @Around("@annotation(registerCodeCheck)")
    public Object around(ProceedingJoinPoint pjp, RegisterCodeCheck registerCodeCheck) throws Throwable {

        HttpServletRequest request = getRequest();

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();

        // 获取方法中的参数的名称
        String[] parameterNames = signature.getParameterNames();
        // 获取方法的参数中的注解
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        // 声明验证码
        String code = "";
        // 声明验证码的 token
        String verifyToken = "";
        // 声明邀请码
        String invitationCode = "";
        // 声明验证码的 token
        String invitationToken = "";
        // 声明用户角色为 null
        RoleType role = null;

        int roleParamIndex = -1;
        int count = 0;
        for (int i = 0; i < parameterAnnotations.length; i++) {
            // 遍历该位置的参数的所有注解
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof VerificationCode) {
                    code = getStringValue(request, parameterNames[i]);
                    count++;
                    break;
                }
                if (annotation instanceof VerificationCodeToken) {
                    verifyToken = getStringValue(request, parameterNames[i]);
                    count++;
                    break;
                }
                if (annotation instanceof InvitationCode) {
                    invitationCode = getStringValue(request, parameterNames[i]);
                    count++;
                    break;
                }
                if (annotation instanceof InvitationCodeToken) {
                    invitationToken = getStringValue(request, parameterNames[i]);
                    count++;
                    break;
                }
                if (annotation instanceof UserRole) {
                    // 获取默认角色
                    RoleType defaultRole = ((UserRole) annotation).defaultRole();
                    // 获取角色 type
                    role = getRole(defaultRole, request, parameterNames[i]);
                    // 获取角色参数所在位置
                    roleParamIndex = i;
                    count++;
                    break;
                }
            }

            if (count == 5) {
                break;
            }
        }

        checkCodes(code, verifyToken, role, invitationCode, invitationToken);

        if (roleParamIndex < 0) {
            // 如果没有使用注解 @UserRole，就抛出异常
            throw new ServiceException(ResultCode.USER_ROLE_NOT_FOUND);
        }

        Object[] args = pjp.getArgs();
        // 将角色的 String 值传入
        args[roleParamIndex] = role.role();
        return pjp.proceed(args);
    }

    @NotNull
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new ServiceException("No attributes available.");
        }

        return attributes.getRequest();
    }

    private String getStringValue(HttpServletRequest request, String parameterName) {

        String parameterValue = request.getParameter(parameterName);
        return parameterValue == null ? "" : parameterValue;
    }

    private RoleType getRole(RoleType defaultRole, HttpServletRequest request, String parameterName) {

        String roleString = request.getParameter(parameterName);

        try {
            // 通过 valueOf 方法直接从大写的字符串中获取相应的 Enum
            return RoleType.valueOf(roleString.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            e.printStackTrace();
            // 找不到的时候，默认返回角色
            return defaultRole;
        }
    }

    /**
     * 检查验证码和邀请码。出错的话抛出相应的异常
     *
     * @param code            验证码
     * @param verifyToken     验证码 token
     * @param role            角色
     * @param invitationCode  邀请码
     * @param invitationToken 邀请码 token
     * @throws ServiceException ResultCode.VERIFICATION_CODE_FAILED
     *                          和 ResultCode.INVITATION_CODE_FAILED
     */
    private void checkCodes(String code,
                            String verifyToken,
                            RoleType role,
                            String invitationCode,
                            String invitationToken) {

        // 如果验证码错误，抛出自定义异常
        verificationCodeService.checkCode(verifyToken, code);

        if (RoleType.ADMIN.equals(role)) {
            // 如果角色是 Admin，需要确认邀请码，如果邀请码出错，会抛出异常
            checkInvitationCode(invitationCode, invitationToken);
        }
    }

    /**
     * 检查邀请码是否正确，如果不正确，抛出异常。
     *
     * @param userTypeInCode  用户输入的邀请码
     * @param invitationToken 邀请码de token
     * @throws ServiceException ResultCode.INVITATION_CODE_FAILED
     */
    private void checkInvitationCode(String userTypeInCode,
                                     String invitationToken) {

        // 获取 token 中的邀请码
        String correctCode = invitationCodeService.getInvitationCode(invitationToken);

        if (ReverseUtils.stringNotEqualsIgnoreCase(userTypeInCode, correctCode)) {
            // 如果 token 中的邀请码和用户输入的不符，就抛出自定义的异常
            throw new ServiceException(ResultCode.INVITATION_CODE_FAILED);
        }
    }
}
