package com.github.learndifferent.mtm.annotation.validation.user.create;

import com.github.learndifferent.mtm.annotation.common.AnnotationHelper;
import com.github.learndifferent.mtm.annotation.common.Password;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.learndifferent.mtm.vo.UserVO;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Verify username and password.
 * If failed verification, throw an exception with one of these result codes according to the situation:
 * <li>{@link ResultCode#USER_ALREADY_EXIST}</li>
 * <li>{@link ResultCode#USERNAME_ONLY_LETTERS_NUMBERS}</li>
 * <li>{@link ResultCode#USERNAME_TOO_LONG}</li>
 * <li>{@link ResultCode#USERNAME_EMPTY}</li>
 * <li>{@link ResultCode#PASSWORD_TOO_LONG}</li>
 * <li>{@link ResultCode#PASSWORD_EMPTY}</li>
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

        AnnotationHelper helper = new AnnotationHelper(2);

        for (int i = 0; i < annotations.length; i++) {
            for (Annotation a : annotations[i]) {
                if (helper.hasNotFoundIndex(0)
                        && a instanceof Username
                        && args[i] != null
                        && String.class.isAssignableFrom(args[i].getClass())) {
                    username = (String) args[i];
                    helper.findIndex(0);
                    break;
                }
                if (helper.hasNotFoundIndex(1)
                        && a instanceof Password
                        && args[i] != null
                        && String.class.isAssignableFrom(args[i].getClass())) {
                    password = (String) args[i];
                    helper.findIndex(1);
                    break;
                }
            }

            if (helper.hasFoundAll()) {
                break;
            }
        }

        checkUsernameAndPassword(username, password);
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