package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.common.Password;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.common.VerificationCode;
import com.github.learndifferent.mtm.annotation.common.VerificationCodeToken;
import com.github.learndifferent.mtm.annotation.general.log.SystemLog;
import com.github.learndifferent.mtm.annotation.validation.login.LoginCheck;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.query.LoginRequest;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Login and Logout
 *
 * @author zhou
 * @date 2021/09/05
 */
@RestController
@RequestMapping("/log")
public class LoginController {

    /**
     * Login
     *
     * @param nameAndPwd  Request Body that contains Username and Password
     * @param code        Verification Code
     * @param verifyToken Token for Verification Code
     * @param userName    Username for Verification (Not Necessary)
     * @param password    Password for Verification (Not Necessary)
     * @return {@code ResultVO<SaTokenInfo>} Token Info
     * @throws ServiceException {@link LoginCheck} will check the login info and throw exceptions if failure.
     *                          The Result Codes are
     *                          {@link com.github.learndifferent.mtm.constant.enums.ResultCode#USER_NOT_EXIST} and
     *                          {@link com.github.learndifferent.mtm.constant.enums.ResultCode#VERIFICATION_CODE_FAILED}
     */
    @LoginCheck
    @PostMapping("/in")
    @SystemLog
    public ResultVO<SaTokenInfo> login(@RequestBody LoginRequest nameAndPwd,
                                       @VerificationCode String code,
                                       @VerificationCodeToken String verifyToken,
                                       @Username String userName,
                                       @Password String password) {

        // Set username as login id
        StpUtil.setLoginId(nameAndPwd.getUserName());
        // Get Token Info and return
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return ResultCreator.okResult(tokenInfo);
    }

    /**
     * Logout
     *
     * @return {@code ResultVO<?>} {@link ResultCreator#okResult()}
     */
    @GetMapping("/out")
    public ResultVO<?> logout() {
        StpUtil.logout();

        return ResultCreator.okResult();
    }

}
