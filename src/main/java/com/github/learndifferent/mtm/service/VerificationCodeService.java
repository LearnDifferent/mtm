package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.exception.ServiceException;

/**
 * 操作验证码
 *
 * @author zhou
 * @date 2021/09/21
 */
public interface VerificationCodeService {

    /**
     * 获取验证码，存入缓存中，并返回验证码图片
     *
     * @param verifyToken 作为缓存的 key
     * @return 返回验证码图片（Base 64）
     * @throws ServiceException 出错的时候抛出异常
     */
    String getVerificationCodeImg(String verifyToken);

    /**
     * 检查验证码是否正确，如果不正确，抛出异常
     *
     * @param verifyToken    存在缓存中的验证码的 key
     * @param userTypeInCode 用户输入的验证码
     * @throws ServiceException 验证码错误异常
     */
    void checkCode(String verifyToken, String userTypeInCode);
}
