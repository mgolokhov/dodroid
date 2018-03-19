package doit.study.droid.data.source;

import java.util.List;


public class Question {

    private long id;
    private String question;
    private List<String> wrong;
    private List<String> right;
    private String docLink;
    private List<String> tags;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getWrong() {
        return wrong;
    }

    public void setWrong(List<String> wrong) {
        this.wrong = wrong;
    }

    public List<String> getRight() {
        return right;
    }

    public void setRight(List<String> right) {
        this.right = right;
    }

    public String getDocLink() {
        return docLink;
    }

    public void setDocLink(String docLink) {
        this.docLink = docLink;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
