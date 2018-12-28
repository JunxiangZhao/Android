package com.example.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.io.IOException;

public class MusicService extends Service {

    final int PLAY_CODE = 1;
    final int STOP_CODE = 2;
    final int TOTAL_TIME = 3;
    final int CUR_POS = 4;
    final int SEEK_BAR = 5;
    final int ADD_CODE = 6;
    int current = 0;
    public MediaPlayer mediaPlayer;

    public MusicService(){
        mediaPlayer = new MediaPlayer();
        try {
            //mediaPlayer.setDataSource("/sdcard/山高水长.mp3");
            mediaPlayer.setDataSource("/sdcard/data/山高水长.mp3");
            mediaPlayer.prepare();
        }catch (IOException e){
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                current = -1;
            }
        });
    }

    public MyBinder binder = new MyBinder();
    public class MyBinder extends Binder {
        @Override
        protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
            switch (code) {
                //playOrPause
                case PLAY_CODE:
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                    }else{
                        mediaPlayer.start();
                    }
                    break;
                //Stop
                case STOP_CODE:
                    mediaPlayer.stop();
                    try {
                        mediaPlayer.prepare();
                        mediaPlayer.seekTo(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                //get total time
                case TOTAL_TIME:
                    reply.writeInt(mediaPlayer.getDuration());
                    break;
                //get current position
                case CUR_POS:
                    if(current == -1){
                        reply.writeInt(-1);
                        current = 0;
                    }
                    else{
                        reply.writeInt(mediaPlayer.getCurrentPosition());
                    }
                    break;
                //seek bar
                case SEEK_BAR:
                    mediaPlayer.seekTo(data.readInt());
                    break;
                case ADD_CODE:
                    mediaPlayer.reset();
                    try {
                        mediaPlayer.setDataSource(data.readString());
                        mediaPlayer.prepare();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
            return super.onTransact(code, data, reply, flags);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent){
        return super.onUnbind(intent);
    }
}
