package com.yiyun.lockcontroller.bean.lock;

import java.io.Serializable;

public class SearchVideoBean implements Serializable {
    private String mLockNo;
    private long mStartTime;
    private long mEndTime;

    public SearchVideoBean(String lockNo, long startTime, long endTime) {
        mLockNo = lockNo;
        mStartTime = startTime;
        mEndTime = endTime;
    }

    public String getLockNo() {
        return mLockNo;
    }

    public void setLockNo(String lockNo) {
        mLockNo = lockNo;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(long startTime) {
        mStartTime = startTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public void setEndTime(long endTime) {
        mEndTime = endTime;
    }
}
