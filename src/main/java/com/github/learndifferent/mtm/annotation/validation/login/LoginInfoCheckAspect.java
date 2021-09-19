package com.github.learndifferent.mtm.annotation.validation.login;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.manager.VerificationCodeManager;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.utils.JsonUtils;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * 检查登陆信息是否正确。
 * 登陆信息从 request 中获取，如果从 param 中没有获取到，就从 body 中获取相应的信息。
 *
 * @author zhou
 * @date 2021/09/05
 */
@Aspect
@Component
@Order(1)
public class LoginInfoCheckAspect {

    private final VerificationCodeManager codeManager;

    private final UserService userService;

    @Autowired
    public LoginInfoCheckAspect(VerificationCodeManager codeManager,
                                UserService userService) {
        this.codeManager = codeManager;
        this.userService = userService;
    }

    /**
     * 验证登陆相关数据，如果出错，就抛出异常
     *
     * @param loginInfoCheck 注解
     * @throws ServiceException 验证出错，就抛出异常
     */
    @Before("@annotation(loginInfoCheck)")
    public void check(LoginInfoCheck loginInfoCheck) {

        // 获取 Request Attributes
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new ServiceException("No Request Attributes Available.");
        }

        // 获取 request。
        // 要在程序中定义 RequestBodyCacheFilter 将 request 转化为 ContentCachingRequestWrapper，
        // 然后通过 FilterConfig 进行配置后，才能重复使用 request body 的数据，防止其像 stream 一样流失
        ContentCachingRequestWrapper request =
                (ContentCachingRequestWrapper) attributes.getRequest();

        // 从注解中，获取需要的变量的名称
        String codeParamName = loginInfoCheck.codeParamName();
        String verifyTokenParamName = loginInfoCheck.verifyTokenParamName();
        String usernameParamName = loginInfoCheck.usernameParamName();
        String passwordParamName = loginInfoCheck.passwordParamName();

        // map's key: param's name; map's value: param's value
        Map<String, String> contents = getContentsFromRequest(request, codeParamName,
                verifyTokenParamName, usernameParamName, passwordParamName);

        // 如果参数的值中，有 null 的话，就从 Request 的 Body 中获取值
        boolean shouldGetValueFromBody = contents.containsValue(null);

        if (shouldGetValueFromBody) {
            // 从 Request Body 中获取 value
            // 如果 value 还是为 null，就转化为空字符串
            renewContentsFromBody(request, contents);
        }

        checkBeforeLogin(contents, codeParamName, verifyTokenParamName,
                usernameParamName, passwordParamName);
    }

    private Map<String, String> getContentsFromRequest(ContentCachingRequestWrapper request,
                                                       String... paramNames) {

        Map<String, String> contents = new HashMap<>(16);

        for (String key : paramNames) {
            String value = request.getParameter(key);
            contents.put(key, value);
        }

        return contents;
    }

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

        // 将 json 字符串转换为 Map<String, String> 并返回
        return JsonUtils.toObject(json, typeRef);
    }

    private void checkBeforeLogin(Map<String, String> contents,
                                  String codeParamName,
                                  String verifyTokenParamName,
                                  String usernameParamName,
                                  String passwordParamName) {

        checkBeforeLogin(contents.get(codeParamName),
                contents.get(verifyTokenParamName),
                contents.get(usernameParamName),
                contents.get(passwordParamName));
    }

    /**
     * 登录前检查
     *
     * @param code        验证码
     * @param verifyToken token
     * @param username    用户名
     * @param password    密码
     * @throws ServiceException 用户不存在：ResultCode.USER_NOT_EXIST
     *                          和验证码错误：ResultCode.VERIFICATION_CODE_FAILED
     */
    private void checkBeforeLogin(String code,
                                  String verifyToken,
                                  String username,
                                  String password) {

        // 如果验证码错误，会抛出错误异常
        codeManager.checkCode(verifyToken, code);

        // 验证码正确，就查找用户
        UserDTO user = userService.getUserByNameAndPwd(username, password);

        if (ObjectUtils.isEmpty(user)) {
            // 如果用户不存在，抛出不存在的异常（也就是用户名或密码不正确）
            throw new ServiceException(ResultCode.USER_NOT_EXIST);
        }
    }

}
