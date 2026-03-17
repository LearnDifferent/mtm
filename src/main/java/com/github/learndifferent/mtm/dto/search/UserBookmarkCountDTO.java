package com.github.learndifferent.mtm.dto.search;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Bookmark count grouped by user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBookmarkCountDTO implements Serializable {

    private Long userId;

    private Integer bookmarkNumber;

    private static final long serialVersionUID = 1L;
}
