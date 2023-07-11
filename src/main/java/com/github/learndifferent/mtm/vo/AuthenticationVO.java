package com.github.learndifferent.mtm.vo;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.github.learndifferent.mtm.dto.IdempotencyKeyInfoDTO;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhou
 * @date 2023/7/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationVO implements Serializable {

    /**
     * Idempotency key information
     */
    private IdempotencyKeyInfoDTO idempotencyKeyInfo;

    /**
     * Token information
     */
    private SaTokenInfo saTokenInfo;

    private static final long serialVersionUID = 1L;
}