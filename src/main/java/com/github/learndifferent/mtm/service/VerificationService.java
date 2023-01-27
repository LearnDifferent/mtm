package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.constant.enums.UserRole;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.query.UserIdentificationRequest;

/**
 * Verification Service
 *
 * @author zhou
 * @date 2021/09/21
 */
public interface VerificationService {

    /**
     * Get the verification code
     *
     * @param token token for verification code
     * @return verification code image with Base64 encoding
     */
    String getVerificationCodeImg(String token);

    /**
     * Check the verification code and invitation code
     *
     * @param code            verification code
     * @param token           token for verification code
     * @param role            user Role
     * @param invitationCode  invitation code
     * @param invitationToken token for invitation code
     * @throws ServiceException If failed verification, this will throw an exception
     *                          with the result code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#VERIFICATION_CODE_FAILED
     *                          VERIFICATION_CODE_FAILED} or {@link com.github.learndifferent.mtm.constant.enums.ResultCode
     *                          INVITATION_CODE_FAILED}
     */
    void checkRegisterCodes(String code, String token, UserRole role, String invitationCode, String invitationToken);

    /**
     * Verify login information and get the username
     *
     * @param userIdentification Request body that contains username and password entered by the user
     * @param token              Token for verification code
     * @param code               Verification code
     * @param isAdmin            check whether the user is the administrator if {@code isAdmin} is true
     * @return username of the user
     * @throws ServiceException Throw an exception with the result code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#VERIFICATION_CODE_FAILED
     *                          VERIFICATION_CODE_FAILED} if the verification code is invalid, or with the result code
     *                          of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#USER_NOT_EXIST
     *                          USER_NOT_EXIST} if username and password do not match.
     *                          When {@code isAdmin} is true the user is not an
     *                          administrator, the result code will be
     *                          {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     */
    String verifyLoginInfoAndGetUsername(UserIdentificationRequest userIdentification,
                                         String token,
                                         String code,
                                         Boolean isAdmin);

    /**
     * Generate invitation code, store the code in cache for 20 minutes),
     * and send the invitation code via email
     *
     * @param token token for verification code
     * @param email email address
     * @throws ServiceException The invitation code will be assigned to the
     *                          "data" field in a {@link ServiceException} when
     *                          an email setting error occurs.
     *                          And the {@link ServiceException} will be thrown
     *                          with the result code of
     *                          {@link com.github.learndifferent.mtm.constant.enums.ResultCode#EMAIL_SET_UP_ERROR
     *                          EMAIL_SET_UP_ERROR} if there is an email setting
     *                          error.
     */
    void sendInvitationCode(String token, String email);
}