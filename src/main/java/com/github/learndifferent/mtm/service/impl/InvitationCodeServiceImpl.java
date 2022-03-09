package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.manager.SendEmailManager;
import com.github.learndifferent.mtm.service.InvitationCodeService;
import com.github.learndifferent.mtm.utils.UUIDUtils;
import java.util.concurrent.TimeUnit;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Generate invitation code, save the code to Redis,
 * send the invitation code via email and get the invitation code.
 *
 * @author zhou
 * @date 2021/9/21
 * @see SendEmailManager mail sending function
 */
@Service
public class InvitationCodeServiceImpl implements InvitationCodeService {

    private final SendEmailManager sendEmailManager;
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public InvitationCodeServiceImpl(SendEmailManager sendEmailManager,
                                     StringRedisTemplate redisTemplate) {
        this.sendEmailManager = sendEmailManager;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Generate invitation code, save the code to Redis (timeout: 20 minutes),
     * and send the invitation code via email
     *
     * @param email email address
     * @throws ServiceException if {@link MessagingException} occurs,
     *                          a {@link ServiceException} will be thrown
     *                          and the invitation code will be assigned to the
     *                          "data" field in {@link ServiceException}
     */
    @Override
    public void send(String invitationToken, String email) {

        // generate invitation code
        String invitationCode = UUIDUtils.getUuid(5);

        // key: token for invitation code
        // value: invitation code
        // timeout: 20 minutes
        redisTemplate.opsForValue().set(invitationToken, invitationCode,
                20, TimeUnit.MINUTES);

        // send email
        try {
            sendEmailManager.sendEmail(email,
                    "MTM - Your Invitation Code",
                    "Invitation Code：<p style=\"color: red\">"
                            + invitationCode
                            + "</p>");
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new ServiceException(
                    ResultCode.EMAIL_SET_UP_ERROR,
                    ResultCode.EMAIL_SET_UP_ERROR.msg(),
                    invitationCode);
        }
    }

    /**
     * Get the invitation code from Redis
     *
     * @param invitationToken token for invitation code
     * @return invitation code
     */
    @Override
    public String getInvitationCode(String invitationToken) {
        return redisTemplate.opsForValue().get(invitationToken);
    }
}
