package doit.study.droid.data.source;

import android.arch.persistence.room.TypeConverters;

import java.util.Date;

import doit.study.droid.data.source.local.Status;

public class Statistic {
    public final int id;
    public int wrongCounter;
    public int rightCounter;
    public int consecutiveRightCnt;
    public boolean checked;
    public Date lastViewedAt;
    public Date studiedAt;
    @TypeConverters({Status.class})
    public Status status;

    public Statistic(int id, int wrongCounter, int rightCounter, int consecutiveRightCnt, boolean checked, Date lastViewedAt, Date studiedAt, Status status) {
        this.id = id;
        this.wrongCounter = wrongCounter;
        this.rightCounter = rightCounter;
        this.consecutiveRightCnt = consecutiveRightCnt;
        this.checked = checked;
        this.lastViewedAt = lastViewedAt;
        this.studiedAt = studiedAt;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Statistic{" +
                "id=" + id +
                ", wrongCounter=" + wrongCounter +
                ", rightCounter=" + rightCounter +
                ", consecutiveRightCnt=" + consecutiveRightCnt +
                ", checked=" + checked +
                ", lastViewedAt=" + lastViewedAt +
                ", studiedAt=" + studiedAt +
                ", status=" + status +
                '}';
    }
}
