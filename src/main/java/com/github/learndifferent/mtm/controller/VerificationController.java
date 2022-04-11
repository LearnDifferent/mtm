package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.validation.user.role.admin.AdminValidation;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.query.UserIdentificationRequest;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Verification Controller
 *
 * @author zhou
 * @date 2021/09/05
 */
@RestController
@RequestMapping("/verification")
public class VerificationController {

    private final VerificationService verificationService;

    @Autowired
    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    /**
     * Get the verification code
     *
     * @param verifyToken token for verification code
     * @return verification code image with Base64 encoding
     */
    @GetMapping("/code")
    public String getVerificationCodeImg(@RequestParam("token") String verifyToken) {

        return verificationService.getVerificationCodeImg(verifyToken);
    }

    /**
     * Send invitation code
     *
     * @param token token for invitation code
     * @param email Email
     * @throws com.github.learndifferent.mtm.exception.ServiceException The invitation code will be assigned to the
     *                                                                  "data" field in a {@code ServiceException} when
     *                                                                  an email setting error occurs.
     *                                                                  And the {@code ServiceException} will be thrown
     *                                                                  with the result code of {@link ResultCode#EMAIL_SET_UP_ERROR}
     *                                                                  if there is an email setting error.
     */
    @GetMapping("/invitation")
    public void send(@RequestParam("token") String token,
                     @RequestParam("email") String email) {

        verificationService.sendInvitationCode(token, email);
    }

    /**
     * Check if the user currently logged in is admin
     *
     * @return {@link ResultCode#SUCCESS} if the current user is admin
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link AdminValidation} annotation
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link ResultCode#PERMISSION_DENIED}
     *                                                                  if the user is not admin
     */
    @GetMapping
    @AdminValidation
    public ResultVO<ResultCode> checkAdmin() {
        return ResultCreator.okResult();
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
    public ResultVO<ResultCode> logout() {
        StpUtil.logout();
        return ResultCreator.okResult();
    }
}