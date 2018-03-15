package doit.study.droid.data.source;

public class Tag {
    public final int id;
    public final String text;
    public final int quantity;
    public final int learned;

    public Tag(int id, String text, int quantity, int learned) {
        this.id = id;
        this.text = text;
        this.quantity = quantity;
        this.learned = learned;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", quantity=" + quantity +
                ", learned=" + learned +
                '}';
    }
}
