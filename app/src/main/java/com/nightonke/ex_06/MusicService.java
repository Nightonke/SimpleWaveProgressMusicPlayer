package com.nightonke.ex_06;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;

/**
 * Created by 伟平 on 2015/11/20.
 */

public class MusicService extends Service {

    private static MediaPlayer player = new MediaPlayer();

    private final IBinder musicBind = new MusicBinder();

    public MusicService() {
        try {
            player.setDataSource("/data/You.mp3");
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void pause() {
        if (player.isPlaying()) {
            player.pause();
        } else {
            player.start();
        }
    }

    public void stop() {
        if (player != null) {
            player.stop();
            try {
                player.prepare();
                player.seekTo(0);
            } catch (Exception e) {

            }
        }
    }

    public String getMusicPositionString() {
        SimpleDateFormat time = new SimpleDateFormat("mm:ss");
        return time.format(player.getCurrentPosition());
    }

    public int getMusicPositionInt() {
        return player.getCurrentPosition();
    }

    public String getMusicLengthString() {
        SimpleDateFormat time = new SimpleDateFormat("mm:ss");
        return time.format(player.getDuration());
    }

    public int getMusicLengthInt() {
        return player.getDuration();
    }

    public String getMusicName() {
        return "You.mp3";
    }

    public void seekTo(int progress) {
        player.seekTo((int)(player.getDuration() * progress / 100f));
    }

    public boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
        super.onDestroy();
    }

}
