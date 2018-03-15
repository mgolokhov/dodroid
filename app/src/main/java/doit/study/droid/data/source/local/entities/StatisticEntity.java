package doit.study.droid.data.source.local.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "statistics")
public class StatisticEntity {
    @PrimaryKey
    public final int id;
    public final int wrongCounter;
    public final int rightCounter;
    public final int consecutiveRightCnt;
    public final boolean checked;
    public final long lastViewedAt;
    public final long studiedAt;
    public final int status;


    public StatisticEntity(int id, int wrongCounter, int rightCounter, int consecutiveRightCnt, boolean checked, long lastViewedAt, long studiedAt, int status) {
        this.id = id;
        this.wrongCounter = wrongCounter;
        this.rightCounter = rightCounter;
        this.consecutiveRightCnt = consecutiveRightCnt;
        this.checked = checked;
        this.lastViewedAt = lastViewedAt;
        this.studiedAt = studiedAt;
        this.status = status;
    }
}
