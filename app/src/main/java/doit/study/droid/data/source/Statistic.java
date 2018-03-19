package doit.study.droid.data.source;

import android.arch.persistence.room.TypeConverters;

import java.util.Date;

import doit.study.droid.data.source.local.Status;

public class Statistic {
    private final long id;
    private int wrongCounter;
    private int rightCounter;
    private int consecutiveRightCnt;
    private boolean checked;
    private Date lastViewedAt;
    private Date studiedAt;
    @TypeConverters({Status.class})
    private Status status;

    public Statistic(long id, int wrongCounter, int rightCounter, int consecutiveRightCnt, boolean checked, Date lastViewedAt, Date studiedAt, Status status) {
        this.id = id;
        this.setWrongCounter(wrongCounter);
        this.setRightCounter(rightCounter);
        this.setConsecutiveRightCnt(consecutiveRightCnt);
        this.setChecked(checked);
        this.setLastViewedAt(lastViewedAt);
        this.setStudiedAt(studiedAt);
        this.setStatus(status);
    }

    @Override
    public String toString() {
        return "Statistic{" +
                "id=" + getId() +
                ", wrongCounter=" + getWrongCounter() +
                ", rightCounter=" + getRightCounter() +
                ", consecutiveRightCnt=" + getConsecutiveRightCnt() +
                ", checked=" + isChecked() +
                ", lastViewedAt=" + getLastViewedAt() +
                ", studiedAt=" + getStudiedAt() +
                ", status=" + getStatus() +
                '}';
    }

    public long getId() {
        return id;
    }

    public int getWrongCounter() {
        return wrongCounter;
    }

    public void setWrongCounter(int wrongCounter) {
        this.wrongCounter = wrongCounter;
    }

    public int getRightCounter() {
        return rightCounter;
    }

    public void setRightCounter(int rightCounter) {
        this.rightCounter = rightCounter;
    }

    public int getConsecutiveRightCnt() {
        return consecutiveRightCnt;
    }

    public void setConsecutiveRightCnt(int consecutiveRightCnt) {
        this.consecutiveRightCnt = consecutiveRightCnt;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Date getLastViewedAt() {
        return lastViewedAt;
    }

    public void setLastViewedAt(Date lastViewedAt) {
        this.lastViewedAt = lastViewedAt;
    }

    public Date getStudiedAt() {
        return studiedAt;
    }

    public void setStudiedAt(Date studiedAt) {
        this.studiedAt = studiedAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
