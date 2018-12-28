package com.example.musicplayer;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    private CircleImageView cover;
    private ImageView playOrPauseBtn, stopBtn, fileBtn, backBtn;
    private TextView musicName, artist, currentPos, totalLen;
    private SeekBar seekBar;
    boolean isPlaying;
    boolean isStoped;
    private MusicService musicService;
    private SimpleDateFormat timeFamate;
    private ObjectAnimator animator;
    private String musicPath;

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.MyBinder)service).getService();

            currentPos.setText(timeFamate.format(musicService.mediaPlayer.getCurrentPosition()));
            totalLen.setText(timeFamate.format(musicService.mediaPlayer.getDuration()));
            //解析歌曲信息
            extractMusicData();

            //播放结束后停止封面旋转，修改按钮
            musicService.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    animator.pause();
                    playOrPauseBtn.setImageResource(R.mipmap.play);
                    isPlaying = false;
                    isStoped = true;
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicService = null;
        }
    };

    Handler mHandler = new Handler();
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(musicService.mediaPlayer.getCurrentPosition());
            seekBar.setMax(musicService.mediaPlayer.getDuration());
            currentPos.setText(timeFamate.format(musicService.mediaPlayer.getCurrentPosition()));
            totalLen.setText(timeFamate.format(musicService.mediaPlayer.getDuration()));
            mHandler.postDelayed(mRunnable, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        //绑定service与activity
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        bindService(intent, sc, BIND_AUTO_CREATE);
        setListener();
    }

    //解析歌曲信息
    private void extractMusicData(){
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(musicPath);
        //获取歌名和歌手
        musicName.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        artist.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
        //获取专辑图片
        byte[] bytes = mediaMetadataRetriever.getEmbeddedPicture();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,bytes.length);
        cover.setImageBitmap(bitmap);
    }

    //初始化所有变量
    private void init(){
        isPlaying = false;
        isStoped = true;
        cover = (CircleImageView)findViewById(R.id.cover);
        playOrPauseBtn = (ImageView)findViewById(R.id.playOrPauseBtn);
        stopBtn = (ImageView)findViewById(R.id.stopBtn);
        fileBtn = (ImageView)findViewById(R.id.fileBtn);
        backBtn = (ImageView)findViewById(R.id.backBtn);
        musicName = (TextView)findViewById(R.id.name);
        artist = (TextView)findViewById(R.id.artist);
        currentPos = (TextView)findViewById(R.id.currentPos);
        totalLen = (TextView)findViewById(R.id.totalLen);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        timeFamate = new SimpleDateFormat("mm:ss");
        animator = ObjectAnimator.ofFloat(cover, "rotation",0f, 360f);
        animator.setDuration(20000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        musicPath = "/sdcard/山高水长.mp3";
    }

    //添加各种按钮和进度条的监听器
    private void setListener(){
        playOrPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.playOrPause();
                if(isPlaying){
                    playOrPauseBtn.setImageResource(R.mipmap.play);
                    animator.pause();
                    isPlaying = false;
                }else{
                    playOrPauseBtn.setImageResource(R.mipmap.pause);
                    if(isStoped){
                        animator.start();
                        isStoped = false;
                    }else{
                        animator.resume();
                    }
                    isPlaying = true;
                    mHandler.post(mRunnable);
                }
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.stop();
                isPlaying = false;
                playOrPauseBtn.setImageResource(R.mipmap.play);
                animator.end();
                isStoped = true;
            }
        });

        fileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                startActivityForResult(intent,0);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(mRunnable);
                unbindService(sc);
                try {
                    MainActivity.this.finish();
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){

                    musicService.mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

    }

    //重置播放器函数，修改所有flag
    private void reset(){
        musicService.mediaPlayer.reset();
        isPlaying = false;
        isStoped = true;
        animator.end();
        playOrPauseBtn.setImageResource(R.mipmap.play);
    }

    //处理选择音频的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(data != null){
            Uri uri = data.getData();
            musicPath = getPath(MainActivity.this,uri);
            try {
                reset();
                musicService.mediaPlayer.setDataSource(musicPath);
                musicService.mediaPlayer.prepare();
                extractMusicData();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //从uri得到真正的物理路径 引用自https://blog.csdn.net/qq_38552744/article/details/78713381
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }
}
