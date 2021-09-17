package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.manager.InvitationCodeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 发送邀请码邮件
 *
 * @author zhou
 * @date 2021/09/05
 */
@Controller
@RequestMapping("/invitation")
public class InvitationCodeController {

    private final InvitationCodeManager invitationCodeManager;

    @Autowired
    public InvitationCodeController(InvitationCodeManager invitationCodeManager) {
        this.invitationCodeManager = invitationCodeManager;
    }

    /**
     * 发送邀请码
     *
     * @param invitationToken 邀请令牌
     * @param email           电子邮件
     * @throws ServiceException 邮件设置错误时，会以 data 的方式返回设置好的验证码
     */
    @GetMapping
    public void send(@RequestParam("invitationToken") String invitationToken,
                     @RequestParam("email") String email) {

        invitationCodeManager.send(invitationToken, email);
    }
}
