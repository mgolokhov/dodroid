package doit.study.droid.data.source.local.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tags")
public class TagEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public final String text;

    public TagEntity(int id, String text) {
        this.id = id;
        this.text = text;
    }

    @Ignore
    public TagEntity(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

}