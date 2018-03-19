package doit.study.droid.data.source.local.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "statistics")
public class StatisticEntity {
    @PrimaryKey
    private final long id;
    private final int wrongCounter;
    private final int rightCounter;
    private final int consecutiveRightCnt;
    private final boolean checked;
    private final long lastViewedAt;
    private final long studiedAt;
    private final int status;


    public StatisticEntity(long id, int wrongCounter, int rightCounter, int consecutiveRightCnt, boolean checked, long lastViewedAt, long studiedAt, int status) {
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

        if (getId() != that.getId()) return false;
        if (getWrongCounter() != that.getWrongCounter()) return false;
        if (getRightCounter() != that.getRightCounter()) return false;
        if (getConsecutiveRightCnt() != that.getConsecutiveRightCnt()) return false;
        if (isChecked() != that.isChecked()) return false;
        if (getLastViewedAt() != that.getLastViewedAt()) return false;
        if (getStudiedAt() != that.getStudiedAt()) return false;
        return getStatus() == that.getStatus();
    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + getWrongCounter();
        result = 31 * result + getRightCounter();
        result = 31 * result + getConsecutiveRightCnt();
        result = 31 * result + (isChecked() ? 1 : 0);
        result = 31 * result + (int) (getLastViewedAt() ^ (getLastViewedAt() >>> 32));
        result = 31 * result + (int) (getStudiedAt() ^ (getStudiedAt() >>> 32));
        result = 31 * result + getStatus();
        return result;
    }

    @Override
    public String toString() {
        return "StatisticEntity{" +
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

    public int getRightCounter() {
        return rightCounter;
    }

    public int getConsecutiveRightCnt() {
        return consecutiveRightCnt;
    }

    public boolean isChecked() {
        return checked;
    }

    public long getLastViewedAt() {
        return lastViewedAt;
    }

    public long getStudiedAt() {
        return studiedAt;
    }

    public int getStatus() {
        return status;
    }
}
