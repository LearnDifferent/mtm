package com.github.learndifferent.mtm.annotation.validation.register;

import com.github.learndifferent.mtm.annotation.common.AnnotationHelper;
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
import com.github.learndifferent.mtm.utils.CompareStringUtil;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
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
 * Verify register information
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

        String[] parameterNames = signature.getParameterNames();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        // verification code
        String code = "";
        // token for verification code
        String verifyToken = "";
        // invitation code
        String invitationCode = "";
        // token for invitation code
        String invitationToken = "";
        // User Role
        RoleType role = null;

        // Index of the parameter that is annotated with @UserRole
        int roleParamIndex = -1;

        AnnotationHelper helper = new AnnotationHelper(5);

        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (helper.hasNotFoundIndex(0)
                        && annotation instanceof VerificationCode) {
                    code = getStringValue(request, parameterNames[i]);
                    helper.findIndex(0);
                    break;
                }
                if (helper.hasNotFoundIndex(1)
                        && annotation instanceof VerificationCodeToken) {
                    verifyToken = getStringValue(request, parameterNames[i]);
                    helper.findIndex(1);
                    break;
                }
                if (helper.hasNotFoundIndex(2)
                        && annotation instanceof InvitationCode) {
                    invitationCode = getStringValue(request, parameterNames[i]);
                    helper.findIndex(2);
                    break;
                }
                if (helper.hasNotFoundIndex(3)
                        && annotation instanceof InvitationCodeToken) {
                    invitationToken = getStringValue(request, parameterNames[i]);
                    helper.findIndex(3);
                    break;
                }
                if (helper.hasNotFoundIndex(4)
                        && annotation instanceof UserRole) {
                    RoleType defaultRole = ((UserRole) annotation).defaultRole();
                    role = getRole(defaultRole, request, parameterNames[i]);
                    // Get the index
                    roleParamIndex = i;
                    helper.findIndex(4);
                    break;
                }
            }

            if (helper.hasFoundAll()) {
                break;
            }
        }

        checkCodes(code, verifyToken, role, invitationCode, invitationToken);

        // throw an exception if no parameter is annotated with @UserRole
        ThrowExceptionUtils.throwIfTrue(roleParamIndex < 0, ResultCode.USER_ROLE_NOT_FOUND);

        Object[] args = pjp.getArgs();
        // assign value to the parameter that is annotated with @UserRole
        args[roleParamIndex] = role.role();
        return pjp.proceed(args);
    }

    @NotNull
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        ThrowExceptionUtils.throwIfNull(attributes, ResultCode.FAILED);

        return attributes.getRequest();
    }

    private String getStringValue(HttpServletRequest request, String parameterName) {

        String parameterValue = request.getParameter(parameterName);
        return Optional.ofNullable(parameterValue).orElse("");
    }

    private RoleType getRole(RoleType defaultRole, HttpServletRequest request, String parameterName) {

        String roleString = request.getParameter(parameterName);

        try {
            // get the RoleType(Enum) from the uppercase string
            return RoleType.valueOf(roleString.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            e.printStackTrace();
            // return default role if can't get the value of role
            return defaultRole;
        }
    }

    /**
     * Check the verification code and invitation code
     *
     * @param code            verification code
     * @param verifyToken     token for verification code
     * @param role            User Role
     * @param invitationCode  invitation code
     * @param invitationToken token for invitation code
     * @throws ServiceException If failed verification, it will throw an exception
     *                          with the result code of {@link ResultCode#VERIFICATION_CODE_FAILED}
     *                          and {@link ResultCode#INVITATION_CODE_FAILED}
     */
    private void checkCodes(String code,
                            String verifyToken,
                            RoleType role,
                            String invitationCode,
                            String invitationToken) {

        // throw exception if failed verification
        verificationCodeService.checkCode(verifyToken, code);

        // If the user is admin, check the invitation code
        if (RoleType.ADMIN.equals(role)) {
            checkInvitationCode(invitationCode, invitationToken);
        }
    }

    /**
     * Check the invitation code
     *
     * @param userTypeInCode  invitation code that user typed in
     * @param invitationToken token for invitation code
     * @throws ServiceException If failed verification, it will throw an exception
     *                          with the result code of {@link ResultCode#INVITATION_CODE_FAILED}
     */
    private void checkInvitationCode(String userTypeInCode,
                                     String invitationToken) {

        // get the correct invitation code according to token
        String correctCode = invitationCodeService.getInvitationCode(invitationToken);

        // verify the invocation code and throw an exception if failed verification
        boolean wrongCode = CompareStringUtil.notEqualsIgnoreCase(userTypeInCode, correctCode);
        ThrowExceptionUtils.throwIfTrue(wrongCode, ResultCode.INVITATION_CODE_FAILED);
    }
}
