package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.general.notification.SystemNotification;
import com.github.learndifferent.mtm.annotation.general.notification.SystemNotification.MessageType;
import com.github.learndifferent.mtm.constant.consist.ErrorInfoConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.query.UserIdentificationRequest;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.VerificationService;
import javax.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
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
@Validated
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
     * @param isAdmin            check whether the user is the administrator if {@code isAdmin} is true
     * @return {@code ResultVO<SaTokenInfo>} Token information
     * @throws com.github.learndifferent.mtm.exception.ServiceException Throw an exception with the result code of
     *                                                                  {@link ResultCode#VERIFICATION_CODE_FAILED} if
     *                                                                  the verification code is invalid or with the
     *                                                                  result code of {@link ResultCode#USER_NOT_EXIST}
     *                                                                  if username and password do not match.
     *                                                                  When {@code isAdmin} is true the user is not an
     *                                                                  administrator, the result code
     *                                                                  will be {@link ResultCode#PERMISSION_DENIED}
     */
    @PostMapping("/login")
    @SystemNotification(messageType = MessageType.LOGIN)
    public ResultVO<SaTokenInfo> login(@RequestBody @Validated UserIdentificationRequest userIdentification,
                                       @RequestParam("token")
                                       @NotBlank(message = "Please ensure that the browser is working properly")
                                               String token,
                                       @RequestParam("code")
                                       @NotBlank(message = ErrorInfoConstant.VERIFICATION_CODE_EMPTY)
                                               String code,
                                       @RequestParam(value = "isAdmin", required = false) Boolean isAdmin) {

        // verify login information and get the username
        String username = verificationService.verifyLoginInfoAndGetUsername(userIdentification, token, code, isAdmin);

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