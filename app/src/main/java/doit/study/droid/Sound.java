package doit.study.droid;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.media.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Sound {
    @SuppressWarnings("unused")
    private final String TAG = "NSA " + getClass().getName();
    private final static String PATH_SOUNDS_WRONG = "wrong";
    private final static String PATH_SOUNDS_RIGHT = "right";
    private String[] mSoundsWrong;
    private String[] mSoundsRight;
    AssetManager mAssetManager;
    MediaPlayer mMediaPlayer;

    public Sound(Context context) {
        Resources res = context.getResources();
        mAssetManager = res.getAssets();
    }

    public static Sound newInstance(Context context) {
        Sound s = new Sound(context);
        // TODO: do it in another thread
        s.mkSoundList();
        return s;
    }

    private void mkSoundList() {
        try {
            mSoundsWrong = mAssetManager.list(PATH_SOUNDS_WRONG);
            mSoundsRight = mAssetManager.list(PATH_SOUNDS_RIGHT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int getRandIndex(String[] list) {
        return new Random().nextInt(list.length);
    }

    public void play(boolean isRight) {
        String curSound;
        if (isRight) {
            curSound = new File(PATH_SOUNDS_RIGHT,
                    mSoundsRight[getRandIndex(mSoundsRight)]).getPath();
        } else {
            curSound = new File(PATH_SOUNDS_WRONG,
                    mSoundsWrong[getRandIndex(mSoundsWrong)]).getPath();
        }
        try {
            AssetFileDescriptor afd = mAssetManager.openFd(curSound);
            this.stop();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            // TODO: async?
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
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
