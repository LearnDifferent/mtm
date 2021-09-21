package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.service.InvitationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Send Invitation Code
 *
 * @author zhou
 * @date 2021/09/05
 */
@Controller
@RequestMapping("/invitation")
public class InvitationCodeController {

    private final InvitationCodeService invitationCodeService;

    @Autowired
    public InvitationCodeController(InvitationCodeService invitationCodeService) {
        this.invitationCodeService = invitationCodeService;
    }

    /**
     * Send Invitation Code
     *
     * @param invitationToken Token of Invitation Code
     * @param email           Email
     * @throws ServiceException If there is an email setting error, {@link InvitationCodeService#send(String, String)}
     *                          will throw an exception that indicates the invitation code
     */
    @GetMapping
    public void send(@RequestParam("invitationToken") String invitationToken,
                     @RequestParam("email") String email) {

        invitationCodeService.send(invitationToken, email);
    }
}
