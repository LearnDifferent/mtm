package com.github.learndifferent.mtm.service;


/**
 * Generate invitation code, save the code to Redis,
 * send the invitation code via email and get the invitation code.
 *
 * @author zhou
 * @date 2021/09/21
 */
public interface InvitationCodeService {

    /**
     * Generate invitation code, save the code to Redis (timeout: 20 minutes),
     * and send the invitation code via email
     *
     * @param email email address
     * @throws com.github.learndifferent.mtm.exception.ServiceException if {@link javax.mail.MessagingException}
     *                                                                  occurs,
     *                                                                  a {@link com.github.learndifferent.mtm.exception.ServiceException}
     *                                                                  will be thrown and the invitation code will be
     *                                                                  assigned to the "data" field in
     *                                                                  {@code ServiceException}
     */
    void send(String invitationToken, String email);

    /**
     * Get the invitation code from Redis
     *
     * @param invitationToken token for invitation code
     * @return invitation code
     */
    String getInvitationCode(String invitationToken);
}
