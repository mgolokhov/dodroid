package doit.study.droid.data.source.local;

import android.arch.persistence.room.TypeConverter;


public enum Status {
    NEW(0), VIEWED(1), STUDIED(2);
    private final int level;

    @TypeConverter
    public static Status fromLevel(Integer level) {
        for (Status p : values()) {
            if (p.level == level) {
                return p;
            }
        }
        return null;
    }

    @TypeConverter
    public static Integer fromPriority(Status p) {
        return p.level;
    }

    Status(int level) {
        this.level = level;
    }
}