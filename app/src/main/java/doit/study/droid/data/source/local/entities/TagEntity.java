package doit.study.droid.data.source.local.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tags")
public class TagEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private final String text;
    private boolean checked;

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    public TagEntity(long id, String text, boolean checked) {
        this.checked = checked;
        this.setId(id);
        this.text = text;
    }

    @Ignore
    public TagEntity(String text, boolean checked) {
        this.text = text;
        this.checked = checked;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setId(long id) {
        this.id = id;
    }
}