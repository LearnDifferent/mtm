package com.github.learndifferent.mtm.annotation.validation.login;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.learndifferent.mtm.annotation.common.AnnotationHelper;
import com.github.learndifferent.mtm.annotation.common.Password;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.common.VerificationCode;
import com.github.learndifferent.mtm.annotation.common.VerificationCodeToken;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.service.VerificationCodeService;
import com.github.learndifferent.mtm.utils.JsonUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.learndifferent.mtm.vo.UserVO;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * Check if the Login Info is valid and get parameter values from Request
 *
 * @author zhou
 * @date 2021/09/05
 */
@Aspect
@Component
@Order(1)
public class VerifyLoginInfoAndGetUsernameAspect {

    private final VerificationCodeService verificationCodeService;
    private final UserService userService;

    @Autowired
    public VerifyLoginInfoAndGetUsernameAspect(VerificationCodeService verificationCodeService,
                                               UserService userService) {
        this.verificationCodeService = verificationCodeService;
        this.userService = userService;
    }

    @Around("@annotation(verifyLoginInfoAndGetParamValue)")
    public Object around(ProceedingJoinPoint pjp, VerifyLoginInfoAndGetParamValue verifyLoginInfoAndGetParamValue)
            throws Throwable {

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();

        String[] parameterNames = signature.getParameterNames();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        String codeParamName = "";
        String verifyTokenParamName = "";
        String usernameParamName = "";
        String passwordParamName = "";

        // the parameters' names that the values may be not present
        List<String> paramsValueMayBeNotPresent = new ArrayList<>();

        // index of the username
        int usernameIndex = -1;

        AnnotationHelper helper = new AnnotationHelper(4);

        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (helper.hasNotFoundIndex(0)
                        && annotation instanceof VerificationCode) {
                    codeParamName = parameterNames[i];
                    helper.findIndex(0);
                    break;
                }
                if (helper.hasNotFoundIndex(1)
                        && annotation instanceof VerificationCodeToken) {
                    verifyTokenParamName = parameterNames[i];
                    helper.findIndex(1);
                    break;
                }
                if (helper.hasNotFoundIndex(2)
                        && annotation instanceof Username) {
                    usernameParamName = parameterNames[i];
                    // if the parameter is not required, then add to the list
                    boolean required = ((Username) annotation).required();
                    boolean mayBeNotPresent = !required;
                    if (mayBeNotPresent) {
                        paramsValueMayBeNotPresent.add(usernameParamName);
                    }

                    // update the index of username
                    usernameIndex = i;

                    helper.findIndex(2);
                    break;
                }
                if (helper.hasNotFoundIndex(3)
                        && annotation instanceof Password) {
                    passwordParamName = parameterNames[i];

                    // if the parameter is not required, then add to the list
                    boolean required = ((Password) annotation).required();
                    boolean mayBeNotPresent = !required;
                    if (mayBeNotPresent) {
                        paramsValueMayBeNotPresent.add(passwordParamName);
                    }

                    helper.findIndex(3);
                    break;
                }
            }

            if (helper.hasFoundAll()) {
                break;
            }
        }

        // if can't find all annotations, throw an exception.
        // this also means that the usernameIndex is greater than -1.
        boolean notFoundAll = !helper.hasFoundAll();
        ThrowExceptionUtils.throwIfTrue(notFoundAll, ResultCode.USER_NOT_EXIST);

        return proceed(pjp, parameterNames, codeParamName, verifyTokenParamName,
                usernameParamName, passwordParamName, paramsValueMayBeNotPresent, usernameIndex);
    }

    private Object proceed(ProceedingJoinPoint pjp,
                           String[] parameterNames,
                           String codeParamName,
                           String verifyTokenParamName,
                           String usernameParamName,
                           String passwordParamName,
                           List<String> paramsValueMayBeNotPresent,
                           int usernameIndex) throws Throwable {
        // 获取可以重复使用的 Request Wrapper
        ContentCachingRequestWrapper request = getRequestWrapper();

        // map's key: param's name; map's value: param's value
        Map<String, String> contents = getParameterNamesAndValues(codeParamName,
                verifyTokenParamName, usernameParamName, passwordParamName, request);

        String properCaseUsername = verifyAndGetProperCaseUsername(contents,
                codeParamName, verifyTokenParamName, usernameParamName, passwordParamName);

        // Get the values
        Object[] args = getArgs(pjp, parameterNames, paramsValueMayBeNotPresent, contents);
        // convert username to proper case
        args[usernameIndex] = properCaseUsername;

        return pjp.proceed(args);
    }

    private Object[] getArgs(ProceedingJoinPoint pjp,
                             String[] parameterNames,
                             List<String> paramsValueMayBeNotPresent,
                             Map<String, String> contents) {
        Object[] args = pjp.getArgs();
        for (String paramName : paramsValueMayBeNotPresent) {
            for (int i = 0; i < parameterNames.length; i++) {
                if (parameterNames[i].equals(paramName)) {
                    args[i] = contents.get(paramName);
                    break;
                }
            }
        }
        return args;
    }

    /**
     * 获取可以重复使用的 Request Wrapper
     *
     * @return {@code ContentCachingRequestWrapper}
     */
    private ContentCachingRequestWrapper getRequestWrapper() {
        // 获取 Request Attributes
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        ThrowExceptionUtils.throwIfNull(attributes, ResultCode.FAILED);

        // 获取 request。
        // 要在程序中定义 RequestBodyCacheFilter 将 request 转化为 ContentCachingRequestWrapper，
        // 然后通过 FilterConfig 进行配置后，才能重复使用 request body 的数据，防止其像 stream 一样流失
        return (ContentCachingRequestWrapper) attributes.getRequest();
    }

    /**
     * 获取参数名称及其值
     *
     * @param codeParamName        验证码参数名称
     * @param verifyTokenParamName 验证码 token 参数名称
     * @param usernameParamName    用户名参数名称
     * @param passwordParamName    密码参数名称
     * @param request              request
     * @return {@code Map<String, String>} key 是参数名称，value 是参数值
     */
    private Map<String, String> getParameterNamesAndValues(String codeParamName,
                                                           String verifyTokenParamName,
                                                           String usernameParamName,
                                                           String passwordParamName,
                                                           ContentCachingRequestWrapper request) {
        // 如果 parameter name（参数名称）为空的话，就将其 value 设置为 null
        Map<String, String> contents = getContentsFromRequest(request, codeParamName,
                verifyTokenParamName, usernameParamName, passwordParamName);

        // 如果参数的值中，有 null 的话，就从 Request 的 Body 中获取值
        boolean shouldGetValueFromBody = contents.containsValue(null);

        if (shouldGetValueFromBody) {
            // 从 Request Body 中获取 value
            // 如果 value 还是为 null，就转化为空字符串
            renewContentsFromBody(request, contents);
        }
        return contents;
    }

    private Map<String, String> getContentsFromRequest(ContentCachingRequestWrapper request,
                                                       String... paramNames) {

        Map<String, String> contents = new HashMap<>(16);

        for (String key : paramNames) {
            String value;
            if (StringUtils.isEmpty(key)) {
                // 如果 Parameter Name 为空，就将其 value 设置为 null
                value = null;
            } else {
                // 如果 Parameter Name 不为空，就从 Request 中获取
                value = request.getParameter(key);
            }
            contents.put(key, value);
        }

        return contents;
    }

    /**
     * 从 Request 中获取更新的内容
     * <p>因为是对 {@code Map<String, String> contents} 的 Reference 进行直接的修改，
     * 所以数据会直接被修改，不需要返回</p>
     *
     * @param request  Request Body
     * @param contents 内容
     */
    private void renewContentsFromBody(ContentCachingRequestWrapper request,
                                       Map<String, String> contents) {

        // 获取请求体，并转换为 Map<String, String>
        Map<String, String> requestBody = getRequestBodyFromRequest(request);

        // 将 request body 的内容直接覆盖进来
        contents.putAll(requestBody);

        // 如果值还是为 null，就转换为空字符串
        contents.replaceAll((k, v) -> v == null ? "" : v);
    }

    /**
     * 获取请求体
     *
     * @param request 请求
     * @return {@code Map<String, String>} key 为参数名，value 为参数值
     */
    private Map<String, String> getRequestBodyFromRequest(ContentCachingRequestWrapper request) {

        // 将 request 中的参数转化为 byte 数组
        byte[] contentAsByteArray = request.getContentAsByteArray();

        // 将该数组转化为字符串
        String json = new String(contentAsByteArray);

        // 获取 Map<String, String> 的 TypeReference，用于下一步的转换操作
        TypeReference<Map<String, String>> typeRef = new TypeReference<Map<String, String>>() {};

        try {
            // 将 json 字符串转换为 Map<String, String> 并返回
            return JsonUtils.toObject(json, typeRef);
        } catch (ServiceException e) {
            // 如果是 JSON_ERROR，表示无法将 Json 字符串转化为 Request Body
            // 说明没有传入 Request Body，也就没办法获取用户名和密码，所以抛出用户不存在的异常
            boolean isJsonError = ResultCode.JSON_ERROR.equals(e.getResultCode());
            ThrowExceptionUtils.throwIfTrue(isJsonError, ResultCode.USER_NOT_EXIST);

            // 其他情况，直接抛出即可
            throw new ServiceException(e.getResultCode(), e.getMessage(), e.getData());
        }
    }

    private String verifyAndGetProperCaseUsername(Map<String, String> contents,
                                                  String codeParamName,
                                                  String verifyTokenParamName,
                                                  String usernameParamName,
                                                  String passwordParamName) {

        return verifyAndGetProperCaseUsername(contents.get(codeParamName),
                contents.get(verifyTokenParamName),
                contents.get(usernameParamName),
                contents.get(passwordParamName));
    }

    /**
     * Check if the Login Info is valid and get the proper case {@code username}
     *
     * @param code                 verification code that the user entered
     * @param verifyToken          token
     * @param username             username
     * @param notEncryptedPassword password that has not been encrypted
     * @throws ServiceException throw an exception with the result code of {@link ResultCode#USER_NOT_EXIST}
     *                          or {@link ResultCode#VERIFICATION_CODE_FAILED} if failure
     */
    private String verifyAndGetProperCaseUsername(String code,
                                                  String verifyToken,
                                                  String username,
                                                  String notEncryptedPassword) {

        verificationCodeService.checkCode(verifyToken, code);

        UserVO user = userService.getUserByNameAndPassword(username, notEncryptedPassword);
        ThrowExceptionUtils.throwIfNull(user, ResultCode.USER_NOT_EXIST);

        return user.getUserName();
    }
}