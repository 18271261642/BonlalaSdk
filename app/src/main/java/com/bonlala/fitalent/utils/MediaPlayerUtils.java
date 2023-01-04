package com.bonlala.fitalent.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import com.bonlala.fitalent.R;
import com.bonlala.fitalent.emu.MediaPlayerType;
import java.io.IOException;
import timber.log.Timber;


/**
 * Created by Admin
 * Date 2023/1/2
 * @author Admin
 */
public class MediaPlayerUtils {

    private static MediaPlayer mediaPlayer;
    AudioManager audioManager;

    /**
     * 初始化播放设置，在播放前设置一遍即可
     * @param context 上下文
     */
    public  void initMediaPlayer(Context context, MediaPlayerType mediaPlayerType){
        Timber.e("--111-----ddddddd="+(mediaPlayer == null));

        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }


        Timber.e("--222-----ddddddd="+(mediaPlayer == null));

       audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
       if(audioManager == null){
           audioManager.requestAudioFocus(audioFocusChangeListener,AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
       }

        AssetFileDescriptor assetFileDescriptor = context.getResources().openRawResourceFd(getResourceId(mediaPlayerType));
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
                    assetFileDescriptor.getStartOffset(),
                    assetFileDescriptor.getLength());

            //是否循环播放
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setVolume(0.5f,0.5f);
                }
            });
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    private int getResourceId(MediaPlayerType mediaPlayerType){
        int id = 0;
        //滴滴
        if(mediaPlayerType == MediaPlayerType.AUDIO_DI_DI){
            id = R.raw.end_sport;
        }

        //倒计时3 2 1
        if(mediaPlayerType == MediaPlayerType.AUDIO_THREE_TWO_ONE){
            id = R.raw.audio_321;
        }

        //3
        if(mediaPlayerType == MediaPlayerType.AUDIO_THREE){
            id = R.raw.audio_3;
        }
        //2
        if(mediaPlayerType == MediaPlayerType.AUDIO_TWO){
            id = R.raw.audio_2;
        }
        //1
        if(mediaPlayerType == MediaPlayerType.AUDIO_ONE){
            id = R.raw.audio_1;
        }




        //倒计时3秒
        if(mediaPlayerType == MediaPlayerType.AUDIO_COUNTDOWN){
            id = R.raw.start_countdown;
        }

        //运动继续
        if(mediaPlayerType == MediaPlayerType.AUDIO_SPORT_CONTINUE){
            id = R.raw.sport_continue;
        }

        //运动休息
        if(mediaPlayerType == MediaPlayerType.AUDIO_SPOT_REST){
            id = R.raw.rest;
        }

        //运动结束
        if(mediaPlayerType == MediaPlayerType.AUDIO_SPORT_END){
            id = R.raw.sport_end;
        }
        //运动开始
        if(mediaPlayerType == MediaPlayerType.AUDIO_SPORT_START){
            id = R.raw.sport_start;
        }
        return id;
    }



    /**
     * 开放播放
     */
    public  void startPlay(){
        Timber.e("-----播放是否为空="+(mediaPlayer == null));
        if(mediaPlayer != null){
            mediaPlayer.start();
        }
    }

    /**
     * 停止播放
     */
    public  void stopPlay(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    private static final AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {
            switch (i){
                case AudioManager.AUDIOFOCUS_LOSS:

                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if(mediaPlayer != null){
                        mediaPlayer.setVolume(0.2f,0.2f);
                    }

                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    if(mediaPlayer != null){
                        mediaPlayer.setVolume(0.5f,0.5f);
                    }
                    break;
                default:
                    break;

            }
        }
    };




    private MediaPlayer audioMedia;
    public void playToAudio(Context context,MediaPlayerType mediaPlayerType){
        if(audioMedia != null){
            audioMedia.stop();
            audioMedia.release();
            audioMedia = null;
        }
        audioMedia = MediaPlayer.create(context,getResourceId(mediaPlayerType));

        audioMedia.start();
    }

    public void playStopToAudio(){
        if(audioMedia != null){
            audioMedia.stop();
        }
    }
}
