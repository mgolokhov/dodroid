//package doit.study.droid.adapters;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentStatePagerAdapter;
//import android.view.ViewGroup;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import doit.study.droid.R;
//import doit.study.droid.data.source.local.entities.QuestionDb;
//import doit.study.droid.fragments.InterrogatorFragment;
//import doit.study.droid.fragments.OneTestSummaryFragment;
//import timber.log.Timber;
//
//
//public class InterrogatorPagerAdapter extends FragmentStatePagerAdapter {
//    private static final boolean DEBUG = false;
//    private List<QuestionDb> mQuestions = new ArrayList<>();
//    private int mSize;
//    private int mRightCnt;
//    private int mWrongCnt;
//    private Context mContext;
//
//    public InterrogatorPagerAdapter(FragmentManager fm, Context c) {
//        super(fm);
//        mContext = c;
//    }
//
//
//    @Override
//    public Fragment getItem(int position) {
////        if (DEBUG) Timber.d("getItem, pos=%d, question=%s", position, mQuestions.get(position));
//        if (position < mQuestions.size()) {
//            return InterrogatorFragment.newInstance(mQuestions.get(position));
//        }
//        else {
//            return OneTestSummaryFragment.newInstance(mWrongCnt, mRightCnt);
//        }
//    }
//
//
//    @Override
//    public Object instantiateItem(ViewGroup container, int position){
//        if (DEBUG) Timber.d("instantiateItem, pos=%d", position);
//        return super.instantiateItem(container, position);
//    }
//
//
//    @Override
//    public int getCount() {
////        if (DEBUG) Timber.d("Size: %d", mSize);
//        return mSize;
//    }
//
//    // don't know why, but getPageTitle called before getItem
//    @Override
//    public CharSequence getPageTitle(int position) {
//        if (position < mQuestions.size()) {
//            if (DEBUG) Timber.d("getPageTitle pos: %d, questions: %s", position, mQuestions.get(position).getId());
//            StringBuffer title = new StringBuffer();
//            // at exit pager asks title, cursor invalid
//            for (String tag : mQuestions.get(position).getTags())
//                title.append(tag).append(" ");
//            title.append(String.format(" %d/%d", position + 1, mQuestions.size()));
//            return title;
//        }
//        else {
//            return mContext.getResources().getString(R.string.test_result_title);
//        }
//    }
//
//    public void addResultPage(int rightCnt, int wrongCnt){
//        mRightCnt = rightCnt;
//        mWrongCnt = wrongCnt;
//        mSize++;
//        notifyDataSetChanged();
//    }
//
//    public void setData(Cursor newCursor){
//        if (DEBUG) Timber.d("setData:id:############");
//        if (mQuestions.size() == 0) {
//            while (newCursor.moveToNext()) {
//                QuestionDb q = QuestionDb.newInstance(newCursor);
//                if (DEBUG) Timber.d("id: %d %s %s", q.getId(), q.getTags(), q.getText());
//                mQuestions.add(q);
//            }
//            mSize = mQuestions.size();
//            notifyDataSetChanged();
//        }
//    }
//}
