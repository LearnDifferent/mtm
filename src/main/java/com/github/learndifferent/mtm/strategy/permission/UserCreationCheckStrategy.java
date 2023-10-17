package com.github.learndifferent.mtm.strategy.permission;

import com.github.learndifferent.mtm.annotation.common.AnnotationHelper;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.Password;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.Username;
import com.github.learndifferent.mtm.constant.consist.ConstraintConstant;
import com.github.learndifferent.mtm.constant.consist.PermissionCheckConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.mapper.UserMapper;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.lang.annotation.Annotation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * User creation check
 *
 * @author zhou
 * @date 2023/10/17
 */
@Component(PermissionCheckConstant.USER_CREATE)
@RequiredArgsConstructor
@Slf4j
public class UserCreationCheckStrategy implements PermissionCheckStrategy {

    private final UserMapper userMapper;

    @Override
    public void checkPermission(Annotation[][] parameterAnnotations, Object[] args) {
        String username = "";
        String password = "";

        AnnotationHelper helper = new AnnotationHelper(Username.class, Password.class);

        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation a : parameterAnnotations[i]) {
                if (helper.hasNotFoundAnnotation(Username.class)
                        && a instanceof Username
                        && args[i] != null
                        && String.class.isAssignableFrom(args[i].getClass())) {
                    username = (String) args[i];
                    helper.findAnnotation(Username.class);
                    break;
                }
                if (helper.hasNotFoundAnnotation(Password.class)
                        && a instanceof Password
                        && args[i] != null
                        && String.class.isAssignableFrom(args[i].getClass())) {
                    password = (String) args[i];
                    helper.findAnnotation(Password.class);
                    break;
                }
            }

            if (helper.hasFoundAllRequiredAnnotations()) {
                break;
            }
        }

        helper.checkIfFoundAllRequiredAnnotations();

        checkUsernameAndPassword(username, password);
    }

    private void checkUsernameAndPassword(String username, String password) {
        checkIfEmpty(username, ResultCode.USERNAME_EMPTY);
        checkIfEmpty(password, ResultCode.PASSWORD_EMPTY);
        checkIfTooLong(password, ConstraintConstant.PASSWORD_MAX_LENGTH, ResultCode.PASSWORD_TOO_LONG);
        checkIfTooLong(username, ConstraintConstant.USERNAME_MAX_LENGTH, ResultCode.USERNAME_TOO_LONG);
        checkIfPasswordTooShort(password);
        checkIfOnlyLetterNumber(username);
        checkIfUsernameDuplicate(username);
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
     */
    private void checkIfTooLong(String str, int length, ResultCode resultCode) {
        boolean tooLong = str.length() > length;
        ThrowExceptionUtils.throwIfTrue(tooLong, resultCode);
    }

    private void checkIfPasswordTooShort(String password) {
        boolean tooShort = password.length() < ConstraintConstant.PASSWORD_MIN_LENGTH;
        ThrowExceptionUtils.throwIfTrue(tooShort, ResultCode.PASSWORD_TOO_SHORT);
    }

    /**
     * Check if string contains only letters and numbers
     *
     * @param str string
     */
    private void checkIfOnlyLetterNumber(String str) {
        String regex = "^[a-z0-9A-Z]+$";
        boolean notMatches = !str.matches(regex);
        ThrowExceptionUtils.throwIfTrue(notMatches, ResultCode.USERNAME_ONLY_LETTERS_NUMBERS);
    }

    /**
     * Check if username is duplicated
     *
     * @param username username
     */
    private void checkIfUsernameDuplicate(String username) {
        boolean isPresent = userMapper.checkIfUsernamePresent(username);
        ThrowExceptionUtils.throwIfTrue(isPresent, ResultCode.USER_ALREADY_EXIST);
    }
}