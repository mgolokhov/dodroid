package doit.study.droid.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "statistics")
public class Statistic {
    @PrimaryKey
    public final int id;
    public final String wrongCounter;
    public final String rightCounter;
    public final String consecutiveRightCnt;
    public final String lastViewedAt;
    public final String studiedAt;


    public Statistic(int id, String wrongCounter, String rightCounter, String consecutiveRightCnt, String lastViewedAt, String studiedAt) {
        this.id = id;
        this.wrongCounter = wrongCounter;
        this.rightCounter = rightCounter;
        this.consecutiveRightCnt = consecutiveRightCnt;
        this.lastViewedAt = lastViewedAt;
        this.studiedAt = studiedAt;
    }
}
