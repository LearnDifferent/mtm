package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * Comment History Data Transfer Object
 *
 * @author zhou
 * @date 2022/4/12
 */
public class CommentHistoryDTO implements Serializable {

    public static CommentHistoryDTO of(long id, long commentId, String comment) {
        return of(id, commentId, comment, Instant.now());
    }

    public static CommentHistoryDTO of(long id, long commentId, String comment, Instant creationTime) {
        return new CommentHistoryDTO(id, commentId, comment, creationTime);
    }

    private CommentHistoryDTO(Long id, Long commentId, String comment, Instant creationTime) {
        this.id = id;
        this.commentId = commentId;
        this.comment = comment;
        this.creationTime = creationTime;
    }

    /**
     * ID
     */
    private final Long id;

    /**
     * ID of the comment
     */
    private final Long commentId;

    /**
     * Comment
     */
    private final String comment;

    /**
     * Creation time
     */
    private final Instant creationTime;

    public Long getCommentId() {
        return commentId;
    }

    public String getComment() {
        return comment;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CommentHistoryDTO that = (CommentHistoryDTO) o;
        return Objects.equals(commentId, that.commentId) && Objects.equals(comment, that.comment)
                && Objects.equals(creationTime, that.creationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId, comment, creationTime);
    }

    @Override
    public String toString() {
        return "CommentHistoryDTO{" +
                "commentId=" + commentId +
                ", comment='" + comment + '\'' +
                ", creationTime=" + creationTime +
                '}';
    }

    private static final long serialVersionUID = 1L;
}