package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck;
import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck.DefaultValueIfEmpty;
import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck.ExceptionIfEmpty;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.service.VerificationCodeService;
import com.github.learndifferent.mtm.utils.VerifyCodeUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

/**
 * 操作验证码
 *
 * @author zhou
 * @date 2021/09/21
 */
@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public VerificationCodeServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获取验证码，存入缓存中，并返回验证码图片
     *
     * @param verifyToken 作为缓存的 key
     * @return 返回验证码图片（Base 64）
     * @throws ServiceException 出错的时候抛出异常
     */
    @Override
    @EmptyStringCheck
    public String getVerificationCodeImg(
            @ExceptionIfEmpty(errorMessage = "没有传入 verifyToken 参数") String verifyToken) {

        // 生成验证码
        String verCode = VerifyCodeUtils.generateVerifyCode(4);

        // 使用 Redis 将传入的 verifyToken 作为 key 存储
        redisTemplate.opsForValue().set(verifyToken, verCode, 5, TimeUnit.MINUTES);

        // 将验证码图片转为 Base 64
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            VerifyCodeUtils.outputImage(230, 60, os, verCode);
            return "data:image/png;base64," + Base64Utils.encodeToString(os.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            // 如果出现异常，将验证码设置为 1234
            redisTemplate.opsForValue().set(verifyToken, "1234", 5, TimeUnit.MINUTES);
            return "http://pic.616pic.com/ys_bnew_img/00/58/36/hETS14h5rO.jpg";
        }
    }

    /**
     * 检查验证码是否正确，如果不正确，抛出异常
     *
     * @param verifyToken    存在缓存中的验证码的 key
     * @param userTypeInCode 用户输入的验证码
     * @throws ServiceException 验证码错误异常
     */
    @Override
    @EmptyStringCheck
    public void checkCode(@DefaultValueIfEmpty String verifyToken,
                          @DefaultValueIfEmpty String userTypeInCode) {

        if (codeNotPassed(verifyToken, userTypeInCode)) {
            throw new ServiceException(ResultCode.VERIFICATION_CODE_FAILED);
        }
    }

    /**
     * 获取缓存中的验证码，并比对用户输入的验证码，查看是否出错
     *
     * @param verifyToken    存在缓存中的验证码的 key
     * @param userTypeInCode 用户输入的验证码
     * @return 验证码错误时，返回 true
     */
    private boolean codeNotPassed(String verifyToken, String userTypeInCode) {

        String code = redisTemplate.opsForValue().get(verifyToken);

        return code == null || !code.trim().equalsIgnoreCase(userTypeInCode);
    }
}
