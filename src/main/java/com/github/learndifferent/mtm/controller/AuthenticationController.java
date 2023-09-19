package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.github.learndifferent.mtm.annotation.general.notification.SystemNotification;
import com.github.learndifferent.mtm.annotation.general.notification.SystemNotification.MessageType;
import com.github.learndifferent.mtm.constant.consist.ErrorInfoConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.IdempotencyKeyInfoDTO;
import com.github.learndifferent.mtm.dto.UserLoginInfoDTO;
import com.github.learndifferent.mtm.query.UserIdentificationRequest;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.VerificationService;
import com.github.learndifferent.mtm.utils.LoginUtils;
import com.github.learndifferent.mtm.vo.AuthenticationVO;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AuthenticationController {

    private final VerificationService verificationService;

    /**
     * Login
     *
     * @param userIdentification Request body that contains username and password entered by the user
     * @param token              token for verification code
     * @param code               verification code
     * @param isAdmin            check whether the user is the administrator if {@code isAdmin} is true
     * @return {@link ResultVO<AuthenticationVO>} token information and idempotency key information
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
    public ResultVO<AuthenticationVO> login(@RequestBody @Validated UserIdentificationRequest userIdentification,
                                            @RequestParam("token")
                                            @NotBlank(message = "Please ensure that the browser is working properly")
                                                    String token,
                                            @RequestParam("code")
                                            @NotBlank(message = ErrorInfoConstant.VERIFICATION_CODE_EMPTY)
                                                    String code,
                                            @RequestParam(value = "isAdmin", required = false) Boolean isAdmin) {

        // verify login information and get the user info
        UserLoginInfoDTO userInfo = verificationService.verifyLoginInfoAndGetUserInfo(
                userIdentification, token, code, isAdmin);

        SaTokenInfo tokenInfo = LoginUtils.setLoginInfoAndGetToken(userInfo);

        // idempotency key information
        long timeout = LoginUtils.getTokenTimeout();
        IdempotencyKeyInfoDTO idempotencyKeyInfo = verificationService.getIdempotencyKeyInfo(timeout);

        AuthenticationVO authentication = AuthenticationVO.builder()
                .idempotencyKeyInfo(idempotencyKeyInfo)
                .saTokenInfo(tokenInfo)
                .build();
        return ResultCreator.okResult(authentication);
    }

    /**
     * Retrieve information about the idempotency key for the authenticated user.
     * The key is used to ensure that duplicate requests are not processed multiple times.
     *
     * @return {@link ResultVO<IdempotencyKeyInfoDTO>} Information about the idempotency key
     */
    @GetMapping("/idempotency-key")
    public ResultVO<IdempotencyKeyInfoDTO> getIdempotencyKeyInfo() {
        long timeout = LoginUtils.getTokenTimeout();
        IdempotencyKeyInfoDTO info = verificationService.getIdempotencyKeyInfo(timeout);
        return ResultCreator.okResult(info);
    }

    /**
     * Logout
     *
     * @return {@link ResultCode#SUCCESS}
     */
    @GetMapping("/logout")
    @SystemNotification(messageType = MessageType.LOGOUT)
    public ResultVO<ResultCode> logout() {
        LoginUtils.logout();
        return ResultCreator.okResult();
    }
}