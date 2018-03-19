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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatisticEntity that = (StatisticEntity) o;

        if (id != that.id) return false;
        if (wrongCounter != that.wrongCounter) return false;
        if (rightCounter != that.rightCounter) return false;
        if (consecutiveRightCnt != that.consecutiveRightCnt) return false;
        if (checked != that.checked) return false;
        if (lastViewedAt != that.lastViewedAt) return false;
        if (studiedAt != that.studiedAt) return false;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + wrongCounter;
        result = 31 * result + rightCounter;
        result = 31 * result + consecutiveRightCnt;
        result = 31 * result + (checked ? 1 : 0);
        result = 31 * result + (int) (lastViewedAt ^ (lastViewedAt >>> 32));
        result = 31 * result + (int) (studiedAt ^ (studiedAt >>> 32));
        result = 31 * result + status;
        return result;
    }

    @Override
    public String toString() {
        return "StatisticEntity{" +
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
