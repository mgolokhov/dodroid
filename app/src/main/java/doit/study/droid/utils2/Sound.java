package doit.study.droid.utils2;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import doit.study.droid.R;
import timber.log.Timber;

public class Sound {
    private final static String PATH_SOUNDS_WRONG = "wrong";
    private final static String PATH_SOUNDS_RIGHT = "right";
    private String[] mSoundsWrong;
    private String[] mSoundsRight;
    AssetManager mAssetManager;
    MediaPlayer mMediaPlayer;

    private static Sound instance;
    private Context mContext;

    private Sound(Context context) {
        mContext = context;
        mAssetManager = context.getResources().getAssets();
        mkSoundList();
    }

    public static synchronized Sound getInstance(Context context){
        if (instance == null)
            instance = new Sound(context.getApplicationContext());
        return instance;
    }

    private void mkSoundList() {
        try {
            mSoundsWrong = mAssetManager.list(PATH_SOUNDS_WRONG);
            mSoundsRight = mAssetManager.list(PATH_SOUNDS_RIGHT);
        } catch (IOException e) {
            Timber.e(e, null);
            soundLoadErrHandler();
        }
    }

    private void soundLoadErrHandler(){
        Toast.makeText(mContext, R.string.cannot_load_sound, Toast.LENGTH_SHORT).show();
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = SP.edit();
        editor.putBoolean(mContext.getResources().getString(R.string.pref_sound), false);
        editor.commit();
    }

    private boolean isEnabled(){
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean prefEnabled = SP.getBoolean(mContext.getResources().getString(R.string.pref_sound), true);
        boolean audioListLoaded = mSoundsWrong != null && mSoundsRight != null;
        // turn off sound (user may turn it on manually again) if audio list is corrupted
        if (prefEnabled && !audioListLoaded)
            soundLoadErrHandler();
        return prefEnabled && audioListLoaded;
    }

    private int getRandIndex(int maxValue) {
        return new Random().nextInt(maxValue);
    }

    public void play(boolean isRight) {
        if (!isEnabled())
            return;
        int randIndex;
        String rootPath;
        String randItem;
        if (isRight) {
            rootPath = PATH_SOUNDS_RIGHT;
            randIndex = getRandIndex(mSoundsRight.length);
            randItem = mSoundsRight[randIndex];
        } else {
            rootPath = PATH_SOUNDS_WRONG;
            randIndex = getRandIndex(mSoundsWrong.length);
            randItem = mSoundsWrong[randIndex];
        }
        String currentSound = new File(rootPath, randItem).getPath();

        try {
            AssetFileDescriptor afd = mAssetManager.openFd(currentSound);
            this.release();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            Timber.e(e, null);
            soundLoadErrHandler();
        }
    }

    public void stop() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying())
            mMediaPlayer.stop();
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
