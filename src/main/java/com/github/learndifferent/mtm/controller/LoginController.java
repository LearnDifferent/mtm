package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.common.Password;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.common.VerificationCode;
import com.github.learndifferent.mtm.annotation.common.VerificationCodeToken;
import com.github.learndifferent.mtm.annotation.general.log.SystemLog;
import com.github.learndifferent.mtm.annotation.validation.login.VerifyLoginInfoAndGetParamValue;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.query.LoginRequest;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Login and Logout
 *
 * @author zhou
 * @date 2021/09/05
 */
@RestController
@RequestMapping("/log")
public class LoginController {

    /**
     * Login
     *
     * @param requestBody Request body that contains username and password.
     *                    The body content is not required if the {@code userName} and {@code password} parameters
     *                    are both present in the request. But if any of them is not present, or any value
     *                    of them is empty, then {@link VerifyLoginInfoAndGetParamValue} annotation will get the values
     *                    from this request body for verification. An exception will be thrown in case both
     *                    {@code userName} parameters, {@code password} parameters and the request body are not present.
     * @param code        Verification Code: {@link VerifyLoginInfoAndGetParamValue} annotation
     *                    will get the value from request and verify the value
     * @param verifyToken Token for Verification Code: {@link VerifyLoginInfoAndGetParamValue}
     *                    annotation will get the value from request and verify the value
     * @param userName    Username for Verification: {@link VerifyLoginInfoAndGetParamValue}
     *                    annotation will get the value from request.
     *                    If the parameter {@code userName} is not present in the request,
     *                    then {@link VerifyLoginInfoAndGetParamValue} annotation will get the value from request body
     *                    and an exception will be thrown in case {@code userName} is not present in the body content.
     *                    <p>
     *                    {@link VerifyLoginInfoAndGetParamValue} annotation will convert {@code userName}
     *                    to proper case as well.
     *                    </p>
     * @param password    Password for Verification: {@link VerifyLoginInfoAndGetParamValue} annotation will get the
     *                    value from request. If the parameter {@code password} is not present in the request,
     *                    then {@link VerifyLoginInfoAndGetParamValue} annotation will get the value from request body
     *                    and an exception will be thrown in case {@code password} is not present in the body content.
     * @return {@code ResultVO<SaTokenInfo>} Token Information
     * @throws ServiceException {@link VerifyLoginInfoAndGetParamValue} annotation will check the login info and throw
     *                          exceptions if failure. The result codes are {@link com.github.learndifferent.mtm.constant.enums.ResultCode#USER_NOT_EXIST}
     *                          and {@link com.github.learndifferent.mtm.constant.enums.ResultCode#VERIFICATION_CODE_FAILED}
     */
    @VerifyLoginInfoAndGetParamValue
    @PostMapping("/in")
    @SystemLog
    public ResultVO<SaTokenInfo> login(@RequestBody(required = false) LoginRequest requestBody,
                                       @VerificationCode String code,
                                       @VerificationCodeToken String verifyToken,
                                       @Username(required = false) String userName,
                                       @Password(required = false) String password) {
        // Set username as login id
        StpUtil.setLoginId(userName);
        // Get Token Info and return
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return ResultCreator.okResult(tokenInfo);
    }

    /**
     * Logout
     *
     * @return {@link ResultCreator#okResult()}
     */
    @GetMapping("/out")
    public ResultVO<ResultCode> logout() {
        StpUtil.logout();

        return ResultCreator.okResult();
    }

}
