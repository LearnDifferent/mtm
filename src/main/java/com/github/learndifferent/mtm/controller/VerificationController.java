package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
}