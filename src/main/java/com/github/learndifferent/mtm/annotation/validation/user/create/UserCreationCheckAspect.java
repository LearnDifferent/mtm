package com.github.learndifferent.mtm.annotation.validation.user.create;

import com.github.learndifferent.mtm.annotation.common.AnnotationHelper;
import com.github.learndifferent.mtm.annotation.common.Password;
import com.github.learndifferent.mtm.annotation.common.UserRole;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.RoleType;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.learndifferent.mtm.vo.UserVO;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Verify username, password and user role.
 * If failed verification, throw exception with the following result codes according to the situation:
 * <p>{@link ResultCode#USER_ALREADY_EXIST}</p>
 * <p>{@link ResultCode#USERNAME_ONLY_LETTERS_NUMBERS}</p>
 * <p>{@link ResultCode#USERNAME_TOO_LONG}</p>
 * <p>{@link ResultCode#USERNAME_EMPTY}</p>
 * <p>{@link ResultCode#PASSWORD_TOO_LONG}</p>
 * <p>{@link ResultCode#PASSWORD_EMPTY}</p>
 * <p>{@link ResultCode#USER_ROLE_NOT_FOUND}</p>
 *
 * @author zhou
 * @date 2021/09/13
 */
@Aspect
@Component
public class UserCreationCheckAspect {

    private final UserService userService;

    @Autowired
    public UserCreationCheckAspect(UserService userService) {
        this.userService = userService;
    }

    @Before("@annotation(annotation)")
    public void check(JoinPoint jp, UserCreationCheck annotation) throws Throwable {

        Object[] args = jp.getArgs();

        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();
        Annotation[][] annotations = method.getParameterAnnotations();

        String username = "";
        String password = "";
        String role = "";

        AnnotationHelper helper = new AnnotationHelper(3);

        for (int i = 0; i < annotations.length; i++) {
            for (Annotation a : annotations[i]) {
                if (helper.hasNotFoundIndex(0)
                        && a instanceof Username
                        && ObjectUtils.isNotEmpty(args[i])
                        && String.class.isAssignableFrom(args[i].getClass())) {
                    username = (String) args[i];
                    helper.findIndex(0);
                    break;
                }
                if (helper.hasNotFoundIndex(1)
                        && a instanceof Password
                        && ObjectUtils.isNotEmpty(args[i])
                        && String.class.isAssignableFrom(args[i].getClass())) {
                    password = (String) args[i];
                    helper.findIndex(1);
                    break;
                }
                if (helper.hasNotFoundIndex(2)
                        && a instanceof UserRole
                        && ObjectUtils.isNotEmpty(args[i])
                        && String.class.isAssignableFrom(args[i].getClass())) {
                    role = (String) args[i];
                    helper.findIndex(2);
                    break;
                }
            }

            if (helper.hasFoundAll()) {
                break;
            }
        }

        checkUserRoleExists(role);
        checkUsernameAndPassword(username, password);
    }

    private void checkUserRoleExists(String role) {
        checkIfEmpty(role, ResultCode.USER_ROLE_NOT_FOUND);
        try {
            // get the RoleType(Enum) from the uppercase string
            RoleType.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            e.printStackTrace();
            // exception when the role is not valid
            throw new ServiceException(ResultCode.USER_ROLE_NOT_FOUND);
        }
    }

    private void checkUsernameAndPassword(String username, String password) {
        checkIfEmpty(username, ResultCode.USERNAME_EMPTY);
        checkIfEmpty(password, ResultCode.PASSWORD_EMPTY);
        checkIfTooLong(password, 50, ResultCode.PASSWORD_TOO_LONG);
        checkIfTooLong(username, 30, ResultCode.USERNAME_TOO_LONG);
        checkIfOnlyLetterNumber(username);
        checkIfUsernameExist(username);
    }

    private void checkIfEmpty(String str, ResultCode resultCode) {
        boolean empty = StringUtils.isEmpty(str);
        ThrowExceptionUtils.throwIfTrue(empty, resultCode);
    }

    /**
     * Check if the string is too long
     *
     * @param str        string
     * @param length     longest length
     * @param resultCode result code
     * @throws ServiceException throw an exception if string is too long
     */
    private void checkIfTooLong(String str, int length, ResultCode resultCode) {
        boolean tooLong = str.length() > length;
        ThrowExceptionUtils.throwIfTrue(tooLong, resultCode);
    }

    /**
     * Check if string contains only letters and numbers
     *
     * @param str string
     * @throws ServiceException if the string contains other characters,
     *                          throw an exception with the result code of {@link ResultCode#USERNAME_ONLY_LETTERS_NUMBERS}
     */
    private void checkIfOnlyLetterNumber(String str) {
        String regex = "^[a-z0-9A-Z]+$";
        boolean notMatches = !str.matches(regex);
        ThrowExceptionUtils.throwIfTrue(notMatches, ResultCode.USERNAME_ONLY_LETTERS_NUMBERS);
    }

    /**
     * Check if username exists
     *
     * @param username username
     * @throws ServiceException if the username is already taken,
     *                          throw an exception with the result code of {@link ResultCode#USER_ALREADY_EXIST}
     */
    private void checkIfUsernameExist(String username) {
        UserVO userHasThatName = userService.getUserByName(username);
        ThrowExceptionUtils.throwIfNotNull(userHasThatName, ResultCode.USER_ALREADY_EXIST);
    }
}