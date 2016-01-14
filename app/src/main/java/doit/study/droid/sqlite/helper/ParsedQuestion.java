package doit.study.droid.sqlite.helper;

import java.util.ArrayList;
import java.util.List;

public class ParsedQuestion {
    public int mTopicId;
    public int mTestSetId;
    public int mQuestionId;
    public String mText;
    public List<String> mWrongItems = new ArrayList<>();
    public List<String> mRightItems = new ArrayList<>();
    public List<String> mTags = new ArrayList<>();
    public String mDocRef;
    public boolean mTrueOrFalse;

    @Override
    public String toString() {
        return mText;
    }
}
