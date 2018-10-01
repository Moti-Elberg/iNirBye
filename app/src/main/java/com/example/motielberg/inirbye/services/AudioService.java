package com.example.motielberg.inirbye.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.IBinder;

import com.example.motielberg.inirbye.R;

public class AudioService extends Service {

    // sfx
    private SoundPool soundPool;
    // music
    private MediaPlayer track1;

    private int click;

    public AudioService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        soundPool=new SoundPool(2, AudioManager.STREAM_MUSIC,0);
        click=soundPool.load(this, R.raw.hh, 1);

        track1=MediaPlayer.create(this, R.raw.mantra);

        track1.start();
        return super.onStartCommand(intent, flags, startId);
    }
}
