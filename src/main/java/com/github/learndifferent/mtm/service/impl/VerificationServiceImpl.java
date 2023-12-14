package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.constant.consist.RedisConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.UserRole;
import com.github.learndifferent.mtm.dto.IdempotencyKeyInfoDTO;
import com.github.learndifferent.mtm.dto.UserLoginInfoDTO;
import com.github.learndifferent.mtm.entity.UserDO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.manager.SendEmailManager;
import com.github.learndifferent.mtm.manager.UserManager;
import com.github.learndifferent.mtm.query.UserIdentificationRequest;
import com.github.learndifferent.mtm.service.VerificationService;
import com.github.learndifferent.mtm.utils.CustomStringUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.learndifferent.mtm.utils.UUIDUtils;
import com.github.learndifferent.mtm.utils.VerifyCodeUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

/**
 * Verification Service Implementation
 *
 * @author zhou
 * @date 2021/09/21
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final StringRedisTemplate redisTemplate;
    private final SendEmailManager sendEmailManager;
    private final UserManager userManager;

    @Value("${idempotency-config.key:mtm-idempotency-key}")
    private String idempotencyKeyHeader;

    @Override
    public String getVerificationCodeImg(String token) {

        // generate verification code
        String code = VerifyCodeUtils.generateVerifyCode(4);
        // store in cache for 5 minutes
        checkAndSaveToRedis(token, code, 5);
        // return verification code image with Base64 encoding
        return getCodeImage(code);
    }

    private String getCodeImage(String code) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            VerifyCodeUtils.outputImage(230, 60, os, code);
            return "data:image/png;base64," + Base64Utils.encodeToString(os.toByteArray());
        } catch (IOException e) {
            log.error("Failed to generate verification code image", e);
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }

    private void checkAndSaveToRedis(String token, String code, int minutes) {
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

    private void checkCode(String token, String code, ResultCode resultCode) {
        // throw an exception if empty
        throwExceptionIfEmpty(token, code, resultCode);

        // get the correct code stored in the cache according to token
        String correctCode = getCodeByToken(token);
        // verify the code and throw an exception if failed verification
        boolean isFailedVerification = CustomStringUtils.notEqualsIgnoreCase(code, correctCode);
        ThrowExceptionUtils.throwIfTrue(isFailedVerification, resultCode);
    }

    private void throwExceptionIfEmpty(String token, String code, ResultCode resultCode) {
        boolean isAnyBlank = StringUtils.isAnyBlank(token, code);
        ThrowExceptionUtils.throwIfTrue(isAnyBlank, resultCode);
    }

    private String getCodeByToken(String token) {
        return redisTemplate.opsForValue().get(token);
    }

    @Override
    public UserLoginInfoDTO verifyLoginInfoAndGetUserInfo(UserIdentificationRequest userIdentification,
                                                          String token,
                                                          String code,
                                                          Boolean shouldCheckIfAdmin) {
        checkCode(token, code, ResultCode.VERIFICATION_CODE_FAILED);

        String username = userIdentification.getUserName();
        String password = userIdentification.getPassword();

        // check if the user exists
        UserDO user = userManager.getUserByNameAndPassword(username, password);
        ThrowExceptionUtils.throwIfNull(user, ResultCode.USER_NOT_EXIST);

        String userRole = user.getRole();
        String adminRole = UserRole.ADMIN.role();

        // verify if the user is an administrator if needed
        if (Boolean.TRUE.equals(shouldCheckIfAdmin)) {
            boolean isCurrentUserNotAdmin = CustomStringUtils.notEqualsIgnoreCase(adminRole, userRole);
            ThrowExceptionUtils.throwIfTrue(isCurrentUserNotAdmin, ResultCode.PERMISSION_DENIED);
        }

        Long userId = user.getId();
        String name = user.getUserName();

        return UserLoginInfoDTO.of(name, userId);
    }

    @Override
    public void sendInvitationCode(String token, String email) {
        // generate invitation code
        String code = UUIDUtils.getUuid(5);
        // store in cache for 20 minutes
        checkAndSaveToRedis(token, code, 20);

        try {
            // send the email
            sendViaEmail(email, code);
        } catch (MessagingException e) {
            log.error("Exception when sending invitation code via email", e);
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

    @Override
    public IdempotencyKeyInfoDTO getIdempotencyKeyInfo(long timeout) {

        // generate idempotency key
        UUID key = UUID.randomUUID();

        // set idempotency key
        String redisKey = RedisConstant.IDEMPOTENCY_KEY_PREFIX + key;
        redisTemplate.opsForValue().set(redisKey, "", timeout, TimeUnit.SECONDS);

        return IdempotencyKeyInfoDTO.builder()
                .idempotencyKey(key)
                .idempotencyKeyHeaderName(idempotencyKeyHeader)
                .build();
    }
}