package com.github.learndifferent.mtm.mapper;

import com.github.learndifferent.mtm.dto.CommentHistoryDTO;
import com.github.learndifferent.mtm.entity.CommentHistoryDO;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * Comment History Mapper
 *
 * @author zhou
 * @date 2022/4/12
 */
@Repository
public interface CommentHistoryMapper {

    /**
     * Create a history record
     *
     * @param commentHistory ID of the comment, comment and creation time
     * @return true if success
     */
    boolean addHistory(CommentHistoryDTO commentHistory);

    /**
     * Get the history of the comment
     *
     * @param commentId ID of the comment
     * @return history
     */
    List<CommentHistoryDO> getHistory(int commentId);
}