package com.github.learndifferent.mtm.service;


/**
 * 生成、发送和接收邀请码
 *
 * @author zhou
 * @date 2021/09/21
 */
public interface InvitationCodeService {

    /**
     * 生成邀请码，存储其到 Redis 中（20 分钟内有效），并发送邮件。
     *
     * @param invitationToken 邀请码的 token
     * @param email           电子邮件地址
     * @throws com.github.learndifferent.mtm.exception.ServiceException 出现 MessagingException 的时候，也就是邮件设置错误，
     *                                                                  会以 data 的方式返回设置好的验证码
     */
    void send(String invitationToken, String email);

    /**
     * 根据 invitationToken，获取存储于其中的邀请码
     *
     * @param invitationToken 邀请码的 token
     * @return {@link String}
     */
    String getInvitationCode(String invitationToken);
}
