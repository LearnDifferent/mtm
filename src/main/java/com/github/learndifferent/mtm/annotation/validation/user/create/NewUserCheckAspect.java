package com.github.learndifferent.mtm.annotation.validation.user.create;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.RoleType;
import com.github.learndifferent.mtm.entity.UserDO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.service.UserService;
import java.lang.reflect.Field;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 检查用户名和密码，以及角色信息是否存在。会抛出 ServiceException 相应的异常。Result code 列表：
 * <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#USER_ALREADY_EXIST}</p>
 * <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#USERNAME_ONLY_LETTERS_NUMBERS}</p>
 * <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#USERNAME_TOO_LONG}</p>
 * <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#USERNAME_EMPTY}</p>
 * <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#PASSWORD_TOO_LONG}</p>
 * <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#PASSWORD_EMPTY}</p>
 * <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#USER_ROLE_NOT_FOUND}</p>
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
            // 如果无法获取，就转换为空字符串
            return "";
        }
    }

    private void checkUserRoleExists(String role) {
        checkIfEmpty(role, ResultCode.USER_ROLE_NOT_FOUND);
        try {
            // 通过 valueOf 方法直接从大写的字符串中获取相应的 Enum
            RoleType.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            e.printStackTrace();
            // 如果无法转换，说明没有传入正确的 role
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

    /**
     * 检查字符串是否为空
     *
     * @param str        字符串
     * @param resultCode 结果状态码
     * @throws ServiceException 抛出相应状态码的异常
     */
    private void checkIfEmpty(String str, ResultCode resultCode) {
        if (StringUtils.isEmpty(str)) {
            throw new ServiceException(resultCode);
        }
    }

    /**
     * 检查字符串是否太长
     *
     * @param str        字符串
     * @param length     长度
     * @param resultCode 结果状态码
     * @throws ServiceException 抛出相应状态码的异常
     */
    private void checkIfTooLong(String str, int length, ResultCode resultCode) {
        if (str.length() > length) {
            throw new ServiceException(resultCode);
        }
    }

    /**
     * 检查字符串是否仅包含英文字母和数字。如果包含除此以外的其他符号，包括空格。如果不是，就抛出异常。
     *
     * @param str str
     * @throws ServiceException ResultCode.USERNAME_ONLY_LETTERS_NUMBERS
     */
    private void checkIfOnlyLetterNumber(String str) {
        String regex = "^[a-z0-9A-Z]+$";
        if (str.matches(regex)) {
            return;
        }
        throw new ServiceException(ResultCode.USERNAME_ONLY_LETTERS_NUMBERS);
    }

    /**
     * 检查用户名是否存在。如果用户名已经在 Database 中存在，就抛出异常。
     *
     * @param username 用户名
     * @throws ServiceException ResultCode.USER_ALREADY_EXIST
     */
    private void checkIfUsernameExist(String username) {
        UserDO userHasThatName = userService.getUserByName(username);
        if (userHasThatName != null) {
            // 如果用户名已经在 Database 中存在，就抛出异常
            throw new ServiceException(ResultCode.USER_ALREADY_EXIST);
        }
    }
}
