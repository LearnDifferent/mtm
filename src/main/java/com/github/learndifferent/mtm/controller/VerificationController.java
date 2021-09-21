package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Get the verification code
 *
 * @author zhou
 * @date 2021/09/05
 */
@RestController
@RequestMapping("/verify")
public class VerificationController {

    private final VerificationCodeService verificationCodeService;

    @Autowired
    public VerificationController(VerificationCodeService verificationCodeService) {
        this.verificationCodeService = verificationCodeService;
    }

    /**
     * Get the verification code
     *
     * @param verifyToken Token stored in localStorage for verification
     * @return Verification code image with Base64 encoding
     */
    @GetMapping("/getVerImg")
    public String getVerificationCodeImg(
            @RequestParam(value = "verifyToken") String verifyToken) {

        return verificationCodeService.getVerificationCodeImg(verifyToken);
    }
}
