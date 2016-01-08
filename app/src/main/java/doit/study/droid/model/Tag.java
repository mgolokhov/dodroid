package doit.study.droid.model;

public class Tag {

    public static final class Table {
        public static final String NAME = "tags";
        public static final String TEXT = "text";
        public static final String SELECTED = "selected";
    }

    private int id;
    private String name;
    private boolean selected;

    public Tag(int id, String name, boolean selected) {
        this.id = id;
        this.name = name;
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
