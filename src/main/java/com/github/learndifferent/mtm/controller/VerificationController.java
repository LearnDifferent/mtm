package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.manager.VerificationCodeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证相关
 *
 * @author zhou
 * @date 2021/09/05
 */
@RestController
@RequestMapping("/verify")
public class VerificationController {

    private final VerificationCodeManager codeManager;

    @Autowired
    public VerificationController(VerificationCodeManager codeManager) {
        this.codeManager = codeManager;
    }

    /**
     * 获取验证码，根据 localStorage 中的 verifyToken 作为 key 存入缓存中，并返回验证码图片
     *
     * @param verifyToken 存储在浏览器的 localStorage 中的 item
     * @return 返回验证码图片（Base 64）
     */
    @GetMapping("/getVerImg")
    public String getVerificationCodeImg(
            @RequestParam(value = "verifyToken") String verifyToken) {

        return codeManager.getVerificationCodeImg(verifyToken);
    }
}
