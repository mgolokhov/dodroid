package doit.study.droid;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import doit.study.droid.data.Question;
import doit.study.droid.data.QuizProvider;
import doit.study.droid.data.Tag;
import timber.log.Timber;


public class TopicsActivity extends ActivityWithDrawer implements LoaderManager.LoaderCallbacks<Cursor>{
    private final static boolean DEBUG = false;
    private TopicAdapter mTopicAdapter;
    private static final int TAG_LOADER = 0;
    private static final int QUESTION_LOADER = 1;
    private List<Tag> mTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.topics_layout, mFrameLayout);
        getSupportLoaderManager().initLoader(TAG_LOADER, null, this);
        getSupportLoaderManager().initLoader(QUESTION_LOADER, null, this);

        RecyclerView rv = (RecyclerView) findViewById(R.id.topics_view);
        rv.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        rv.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider));
        mTopicAdapter = new TopicAdapter();
        rv.setAdapter(mTopicAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_topic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select_all:
                setSelectionToAllTags(true);
                return true;
            case R.id.unselect_all:
                setSelectionToAllTags(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setSelectionToAllTags(boolean checked){
        for (Tag tag: mTags)
            tag.setChecked(checked);
        mTopicAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        new Thread(){
            @Override
            public void run() {
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                ContentProviderOperation.Builder builder;
                for (Tag tag: mTopicAdapter.getTags()) {
                    builder = ContentProviderOperation.newUpdate(QuizProvider.TAG_URI);
                    builder.withValue(Tag.Table.SELECTED, tag.getSelectionStatus());
                    builder.withSelection(Tag.Table._ID + " = " + tag.getId(), null);
                    ops.add(builder.build());
                }
                try {
                    getContentResolver().applyBatch(QuizProvider.AUTHORITY, ops);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        super.onPause();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id){
            case TAG_LOADER:
                return new CursorLoader(this, QuizProvider.TAG_URI, null, null, null, null);
            case QUESTION_LOADER:
                return new CursorLoader(this, QuizProvider.QUESTION_URI, new String[]{Question.Table._ID}, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch(loader.getId()){
            case TAG_LOADER:
                while(data.moveToNext()){
                    mTags.add(Tag.newInstance(data));
                }
                mTopicAdapter.setTags(mTags);
                break;
            case QUESTION_LOADER:
                setTitle("Total questions: " + data.getCount());
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch(loader.getId()){
            case TAG_LOADER:
                mTopicAdapter.swapCursor(null);
                break;
            case QUESTION_LOADER:
                break;
        }
    }


    private static class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder>{
        private List<Tag> mTags;
        private Cursor mCursor;

        public static class TopicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView topic;
            CheckBox checkbox;
            public TopicViewHolder(View itemView) {
                super(itemView);
                topic = (TextView)itemView.findViewById(R.id.topic_name);
                checkbox = (CheckBox) itemView.findViewById(R.id.checkbox_tag);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
//                Toast.makeText(v.getContext(), topic.getText(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public int getItemCount() {
            if (mTags != null)
                return mTags.size();
            else
                return 0;
        }


        public List<Tag> getTags(){
            return mTags;
        }

        public void setTags(List<Tag> tags){
            mTags = tags;
            notifyDataSetChanged();
        }

        @Override
        public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_item, parent, false);
            return new TopicViewHolder(v);
        }

        @Override
        public void onBindViewHolder(TopicViewHolder holder, int position) {
            final Tag tag = mTags.get(position);
            if (DEBUG) Timber.d(tag.toString()+" "+position);
            String text = String.format("%s (%d/%d)", tag.getName(), tag.getQuestionsCounter(), tag.getQuestionsStudied());
            holder.topic.setText(text);
            holder.checkbox.setChecked(tag.getSelectionStatus());
            holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tag.setChecked(((CheckBox)v).isChecked());
                if (DEBUG) Timber.d("change " + tag);
            }
        });
        }
        public void swapCursor(Cursor newCursor) {
            mCursor = newCursor;
            notifyDataSetChanged();
        }

        public Cursor getCursor() {
            return mCursor;
        }
    }

    private static class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private final int[] ATTRS = new int[]{android.R.attr.listDivider};

        private Drawable mDivider;

        /**
         * Default divider will be used
         */
        public DividerItemDecoration(Context context) {
            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
            mDivider = styledAttributes.getDrawable(0);
            styledAttributes.recycle();
        }

        /**
         * Custom divider will be used
         */
        public DividerItemDecoration(Context context, int resId) {
            mDivider = ContextCompat.getDrawable(context, resId);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}

