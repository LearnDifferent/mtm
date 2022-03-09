package com.github.learndifferent.mtm.annotation.validation.user.create;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.RoleType;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.lang.reflect.Field;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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
public class NewUserCheckAspect {

    private final UserService userService;

    @Autowired
    public NewUserCheckAspect(UserService userService) {
        this.userService = userService;
    }

    @Around("@annotation(annotation)")
    public Object around(ProceedingJoinPoint pjp, NewUserCheck annotation) throws Throwable {

        Object[] args = pjp.getArgs();

        Class<?> cls = annotation.userClass();
        String usernameFieldName = annotation.usernameFieldName();
        String passwordFieldName = annotation.passwordFieldName();
        String roleFieldName = annotation.roleFieldName();

        String username = "";
        String password = "";
        String role = "";
        for (Object arg : args) {
            if (arg != null && cls.isAssignableFrom(arg.getClass())) {
                username = getFieldValue(cls, usernameFieldName, arg);
                password = getFieldValue(cls, passwordFieldName, arg);
                role = getFieldValue(cls, roleFieldName, arg);
                break;
            }
        }

        checkUserRoleExists(role);
        checkUsernameAndPassword(username, password);
        return pjp.proceed();
    }

    private String getFieldValue(Class<?> cls, String fieldName, Object arg) {
        try {
            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(arg);
            return (String) value;
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            e.printStackTrace();
            // use empty string if can't get the value
            return "";
        }
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
        UserDTO userHasThatName = userService.getUserByName(username);
        ThrowExceptionUtils.throwIfNotNull(userHasThatName, ResultCode.USER_ALREADY_EXIST);
    }
}
