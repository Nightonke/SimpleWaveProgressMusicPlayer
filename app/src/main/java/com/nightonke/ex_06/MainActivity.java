package com.nightonke.ex_06;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.john.waveview.WaveView;

public class MainActivity extends AppCompatActivity {

    private WaveView musicProgress;

    private MusicService musicService;

    private Button play;
    private Button stop;
    private Button quit;

    private TextView musicName;
    private TextView musicPlayPosition;
    private TextView musicLength;

    private int currentProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicProgress = (WaveView)findViewById(R.id.music_progress);
        musicProgress.setProgress(currentProgress);

        play = (Button)findViewById(R.id.play);
        stop = (Button)findViewById(R.id.stop);
        quit = (Button)findViewById(R.id.quit);

        musicName = (TextView)findViewById(R.id.music_name);
        musicPlayPosition = (TextView)findViewById(R.id.play_time);
        musicPlayPosition.setText("00:00");
        musicLength = (TextView)findViewById(R.id.music_length);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play.setText(musicService.isPlaying() ? "Pause" : "Play");
                musicService.pause();
                handler.post(r);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.stop();
                currentProgress = 0;
                play.setText(musicService.isPlaying() ? "Pause" : "Play");
            }
        });

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bindServiceConnection();
    }

    private void bindServiceConnection() {
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        bindService(intent, musicConnection, this.BIND_AUTO_CREATE);
    }

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.MusicBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
        }
    };

    @Override
    public void onBackPressed() {
        handler.removeCallbacks(r);
        unbindService(musicConnection);
        try {
            System.exit(0);
        } catch (Exception e) {

        }
        super.onBackPressed();
    }

    Handler handler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
            musicPlayPosition.setText(musicService.getMusicPositionString());
            musicLength.setText(musicService.getMusicLengthString());
            musicProgress.setProgress((int) (1.0 * musicService.getMusicPositionInt()
                    / musicService.getMusicLengthInt() * 100));
            play.setText(musicService.isPlaying() ? "Pause" : "Play");
            handler.postDelayed(r, 100);
        }
    };

    private float x1, x2, y1, y2;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = ev.getX();
                y1 = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (!musicService.isPlaying()) break;
                x2 = ev.getX();
                y2 = ev.getY();
                if (Math.abs(y2 - y1) > Math.abs(x2 - x1)) {
                    if (y2 - y1 > 100) {
                        // wave down
                        float num = (y2 - y1) / 100;
                        currentProgress -= num;
                        if (currentProgress < 0) currentProgress = 0;
                    }
                    if (y1 - y2 > 100) {
                        // wave up
                        float num = (y1 - y2) / 100;
                        currentProgress += num;
                        if (currentProgress > 100) currentProgress = 100;
                    }
                    musicProgress.setProgress(currentProgress);
                    musicService.seekTo(currentProgress);
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

}
