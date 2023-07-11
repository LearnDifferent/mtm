package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Idempotency key information
 *
 * @author zhou
 * @date 2023/7/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdempotencyKeyInfoDTO implements Serializable {

    /**
     * The name of the HTTP header used to convey an idempotency key for the request
     */
    private String idempotencyKeyHeaderName;

    /**
     * Idempotency key
     */
    private UUID idempotencyKey;

    private static final long serialVersionUID = 1L;
}