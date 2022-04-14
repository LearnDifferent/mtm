package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.general.notification.SystemNotification;
import com.github.learndifferent.mtm.annotation.general.notification.SystemNotification.MessageType;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.query.UserIdentificationRequest;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.VerificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication
 *
 * @author zhou
 * @date 2022/4/12
 */
@RestController
public class AuthenticationController {

    private final VerificationService verificationService;

    public AuthenticationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    /**
     * Login
     *
     * @param userIdentification Request body that contains username and password entered by the user
     * @param token              token for verification code
     * @param code               verification code
     * @return {@code ResultVO<SaTokenInfo>} Token information
     * @throws com.github.learndifferent.mtm.exception.ServiceException Throw an exception with the result code of
     *                                                                  {@link ResultCode#VERIFICATION_CODE_FAILED} if
     *                                                                  the verification code is invalid and with the
     *                                                                  result code of {@link ResultCode#USER_NOT_EXIST}
     *                                                                  if username and password do not match
     */
    @PostMapping("/login")
    @SystemNotification(messageType = MessageType.LOGIN)
    public ResultVO<SaTokenInfo> login(@RequestBody UserIdentificationRequest userIdentification,
                                       @RequestParam("token") String token,
                                       @RequestParam("code") String code) {

        // verify login information and get the username
        String username = verificationService.verifyLoginInfoAndGetUsername(userIdentification, token, code);

        // set username as login ID
        StpUtil.setLoginId(username);

        // return token information
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return ResultCreator.okResult(tokenInfo);
    }

    /**
     * Logout
     *
     * @return {@link ResultCode#SUCCESS}
     */
    @GetMapping("/logout")
    @SystemNotification(messageType = MessageType.LOGOUT)
    public ResultVO<ResultCode> logout() {
        StpUtil.logout();
        return ResultCreator.okResult();
    }
}