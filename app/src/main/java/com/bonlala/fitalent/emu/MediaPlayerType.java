package com.bonlala.fitalent.emu;

/**
 * 播放音频的枚举
 * Created by Admin
 * Date 2023/1/2
 * @author Admin
 */
public enum MediaPlayerType {

    //滴滴声，
    AUDIO_DI_DI,

    //3 2 1
    AUDIO_THREE_TWO_ONE,

    //3
    AUDIO_THREE,
    //2
    AUDIO_TWO,
    //1
    AUDIO_ONE,


    //倒计时3秒
    AUDIO_COUNTDOWN,

    //运动开始 -语音
    AUDIO_SPORT_START,

    //运动中途休息 -休息一下吧
    AUDIO_SPOT_REST,

    //运动继续
    AUDIO_SPORT_CONTINUE,

    //运动结束
    AUDIO_SPORT_END;


    private MediaPlayerType() {
    }
}
