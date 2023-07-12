package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.general.idempotency.IdempotencyCheck;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Send verification code and invitation code
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
    @GetMapping("/invitation-code")
    @IdempotencyCheck
    public void send(@RequestParam("token") String token,
                     @RequestParam("email") String email) {

        verificationService.sendInvitationCode(token, email);
    }
}