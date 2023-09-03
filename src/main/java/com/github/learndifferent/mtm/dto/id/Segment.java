package com.github.learndifferent.mtm.dto.id;

import java.util.concurrent.atomic.AtomicLong;
import lombok.Data;

/**
 * Segment for storing IDs
 *
 * @author zhou
 * @date 2023/9/3
 */
@Data
public class Segment {

    /**
     * Current ID
     */
    private AtomicLong currentId = new AtomicLong(0L);

    /**
     * Max ID
     */
    private volatile long maxId;

    private SegmentBuffer buffer;

    public Segment(SegmentBuffer buffer) {
        this.buffer = buffer;
    }

    /**
     * Remaining ID count
     *
     * @return remaining ID count
     */
    public long getRemainingIdCount() {
        return maxId - currentId.get();
    }
}