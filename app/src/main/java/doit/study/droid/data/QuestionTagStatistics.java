package doit.study.droid.data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;


public class QuestionTagStatistics {

    public int id;
    public String question;
    public List<String> wrong;
    public List<String> right;
    public String docLink;
//    public List<String> tags;
}
