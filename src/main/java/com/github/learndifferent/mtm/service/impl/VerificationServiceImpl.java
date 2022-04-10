package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.UserRole;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.manager.SendEmailManager;
import com.github.learndifferent.mtm.service.VerificationService;
import com.github.learndifferent.mtm.utils.CompareStringUtil;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.learndifferent.mtm.utils.UUIDUtils;
import com.github.learndifferent.mtm.utils.VerifyCodeUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

/**
 * Verification Service Implementation
 *
 * @author zhou
 * @date 2021/09/21
 */
@Service
public class VerificationServiceImpl implements VerificationService {

    private final StringRedisTemplate redisTemplate;
    private final SendEmailManager sendEmailManager;

    @Autowired
    public VerificationServiceImpl(StringRedisTemplate redisTemplate,
                                   SendEmailManager sendEmailManager) {
        this.redisTemplate = redisTemplate;
        this.sendEmailManager = sendEmailManager;
    }

    @Override
    public String getVerificationCodeImg(String token) {

        // generate verification code
        String code = VerifyCodeUtils.generateVerifyCode(4);
        // store in cache for 5 minutes
        storeInCache(token, code, 5);
        // return verification code image with Base64 encoding
        return getCodeImage(token, code);
    }

    private String getCodeImage(String token, String code) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            VerifyCodeUtils.outputImage(230, 60, os, code);
            return "data:image/png;base64," + Base64Utils.encodeToString(os.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            // make the code be 1234 if something goes wrong
            storeInCache(token, "1234", 5);
            return "http://pic.616pic.com/ys_bnew_img/00/58/36/hETS14h5rO.jpg";
        }
    }

    private void storeInCache(String token, String code, int minutes) {
        throwExceptionIfEmpty(token, code, ResultCode.FAILED);
        redisTemplate.opsForValue().set(token, code, minutes, TimeUnit.MINUTES);
    }

    @Override
    public void checkRegisterCodes(String code,
                                   String token,
                                   UserRole role,
                                   String invitationCode,
                                   String invitationToken) {

        // check verification code
        checkCode(token, code, ResultCode.VERIFICATION_CODE_FAILED);

        if (UserRole.ADMIN.equals(role)) {
            // check invitation code if the user is admin
            checkCode(invitationToken, invitationCode, ResultCode.INVITATION_CODE_FAILED);
        }
    }

    @Override
    public void checkVerificationCode(String token, String code) {
        checkCode(token, code, ResultCode.VERIFICATION_CODE_FAILED);
    }

    private void checkCode(String token, String code, ResultCode resultCode) {
        // throw an exception if empty
        throwExceptionIfEmpty(token, code, resultCode);

        // get the correct code stored in the cache according to token
        String correctCode = getCodeByToken(token);
        // verify the code and throw an exception if failed verification
        boolean isFailedVerification = CompareStringUtil.notEqualsIgnoreCase(code, correctCode);
        ThrowExceptionUtils.throwIfTrue(isFailedVerification, resultCode);
    }

    private void throwExceptionIfEmpty(String token, String code, ResultCode resultCode) {
        boolean isEmpty = StringUtils.isEmpty(token) || StringUtils.isEmpty(code);
        ThrowExceptionUtils.throwIfTrue(isEmpty, resultCode);
    }

    private String getCodeByToken(String token) {
        return redisTemplate.opsForValue().get(token);
    }

    @Override
    public void sendInvitationCode(String token, String email) {
        // generate invitation code
        String code = UUIDUtils.getUuid(5);
        // store in cache for 20 minutes
        storeInCache(token, code, 20);

        try {
            // send the email
            sendViaEmail(email, code);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new ServiceException(
                    ResultCode.EMAIL_SET_UP_ERROR,
                    ResultCode.EMAIL_SET_UP_ERROR.msg(),
                    code);
        }
    }

    private void sendViaEmail(String email, String invitationCode) throws MessagingException {
        sendEmailManager.sendEmail(email,
                "MTM - Your Invitation Code",
                "Invitation Code：<p style=\"color: red\">"
                        + invitationCode
                        + "</p>");
    }
}