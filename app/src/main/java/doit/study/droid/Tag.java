package doit.study.droid;

public class Tag {
    private int id;
    private String name;

    public static class Stats {
        int questionsCount;
        int learned;

        public Stats(int questionsCount, int learned) {
            this.questionsCount = questionsCount;
            this.learned = learned;
        }

        public int getQuestionsCount() {
            return questionsCount;
        }

        public int getLearned() {
            return learned;
        }
    }

    public Tag(int id, String name) {
        this.id = id;
        this.name = name;
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
