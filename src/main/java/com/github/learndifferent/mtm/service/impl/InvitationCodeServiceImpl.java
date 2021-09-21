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
 * @author zhou
 * @date 2021/9/21
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
     * 生成邀请码，存储其到 Redis 中（20 分钟内有效），并发送邮件。
     *
     * @param email 电子邮件地址
     * @throws ServiceException 出现 MessagingException 的时候，也就是邮件设置错误，
     *                          会以 data 的方式返回设置好的验证码
     */
    @Override
    public void send(String invitationToken, String email) {

        // 生成邀请码
        String invitationCode = UUIDUtils.getUuid(5);

        // key 是 invitationToken 的值，value 是邀请码，时间设定为 20 分钟
        redisTemplate.opsForValue().set(invitationToken, invitationCode,
                20, TimeUnit.MINUTES);

        // 发送邮件
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
     * 根据 invitationToken，获取存储于其中的邀请码
     *
     * @return {@code String}
     */
    @Override
    public String getInvitationCode(String invitationToken) {
        return redisTemplate.opsForValue().get(invitationToken);
    }
}
