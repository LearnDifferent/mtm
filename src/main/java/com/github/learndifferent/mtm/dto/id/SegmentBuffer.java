package com.github.learndifferent.mtm.dto.id;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A segment buffer to store the segments and information related to a specific business tag
 *
 * @author zhou
 * @date 2023/9/3
 */
public class SegmentBuffer {

    /**
     * Business tag
     */
    private String tag;

    /**
     * Double buffer
     */
    private final Segment[] segments;

    /**
     * Index of current segment in {@link #segments}
     */
    private volatile int currentSegmentIndex;

    /**
     * true if the next segment is ready to switch
     */
    private volatile boolean isNextSegmentReady;

    /**
     * True if initialization successful
     */
    private volatile boolean isInit;

    /**
     * True if the thread is running
     */
    private final AtomicBoolean isThreadRunning;

    private final ReadWriteLock readWriteLock;

    public SegmentBuffer() {
        segments = new Segment[]{new Segment(this), new Segment(this)};
        currentSegmentIndex = 0;
        isInit = false;
        isThreadRunning = new AtomicBoolean(false);
        readWriteLock = new ReentrantReadWriteLock();
    }

    public Segment getCurrentSegment() {
        return segments[currentSegmentIndex];
    }

    public int getNexSegmentIndex() {
        return (currentSegmentIndex + 1) % 2;
    }

    public void switchCurrentSegmentIndex() {
        currentSegmentIndex = getNexSegmentIndex();
    }

    public Lock getReadLock() {
        return readWriteLock.readLock();
    }

    public Lock getWriteLock() {
        return readWriteLock.writeLock();
    }

    /**
     * Get the {@link #isThreadRunning} object for record if the thread is running
     *
     * @return {@link #isThreadRunning}
     */
    public AtomicBoolean getThreadRunningAtomicBoolean() {
        return isThreadRunning;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Segment[] getSegments() {
        return segments;
    }

    public boolean isNextSegmentReady() {
        return isNextSegmentReady;
    }

    public void setNextSegmentReady(boolean nextSegmentReady) {
        isNextSegmentReady = nextSegmentReady;
    }

    public boolean hasNotInit() {
        return !isInit;
    }

    public void setInit(boolean init) {
        this.isInit = init;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SegmentBuffer that = (SegmentBuffer) o;
        return currentSegmentIndex == that.currentSegmentIndex && isNextSegmentReady == that.isNextSegmentReady
                && isInit == that.isInit && Objects.equals(tag, that.tag) && Arrays.equals(segments,
                that.segments) && Objects.equals(isThreadRunning, that.isThreadRunning)
                && Objects.equals(readWriteLock, that.readWriteLock);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(tag, currentSegmentIndex, isNextSegmentReady, isInit, isThreadRunning, readWriteLock);
        result = 31 * result + Arrays.hashCode(segments);
        return result;
    }

    @Override
    public String toString() {
        return "SegmentBuffer{" +
                "tag='" + tag + '\'' +
//                ", segments=" + Arrays.toString(segments) +
                ", currentSegmentIndex=" + currentSegmentIndex +
                ", isNextSegmentReady=" + isNextSegmentReady +
                ", isInit=" + isInit +
                ", isThreadRunning=" + isThreadRunning +
                ", readWriteLock=" + readWriteLock +
                '}';
    }
}