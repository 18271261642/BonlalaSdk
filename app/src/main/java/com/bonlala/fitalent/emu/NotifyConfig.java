package com.bonlala.fitalent.emu;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin
 * Date 2022/10/31
 * @author Admin
 */
public class NotifyConfig {


    /**QQ**/
    private static final String QQ_PACKAGE_NAME = "com.tencent.mobileqq";
    /**QQ急速版**/
    private static final String QQ_FAST_PACK_NAME = "com.tencent.qqlite";

    /**电话的通知,未接来电时的提醒**/
    private static final String TEL_NOTIFICATION_NAME = "com.android.server.telecom";

    /**微信**/
    private static final String WECHAT_PACKAGE_NAME = "com.tencent.mm";
    /**微博**/
    private static final String WEIBO_PACKAGE_NAME = "com.sina.weibo";
    /**Facebook**/
    private static final String FACEBOOK_PACKAGE_NAME = "com.facebook.katana";

    private static final String FACEBOOK_PACKAGE_NAME1 = "com.facebook.orca";
    /**twitter**/
    private static final String TWITTER_PACKAGE_NAME = "com.twitter.android";
    /**Whats**/
    private static final String WHATS_PACKAGE_NAME = "com.whatsapp";
    /**viber**/
    private static final String VIBER_PACKAGE_NAME = "com.viber.voip";
    /**instagram**/
    private static final String INSTANRAM_PACKAGE_NAME = "com.instagram.android";
    /**日历**/
    private static final String CALENDAR_PACKAGE_NAME = "com.android.calendar";
    /**信息 三星手机信息**/
    private static final String SAMSUNG_MSG_PACK_NAME = "com.samsung.android.messaging";
    private static final String SAMSUNG_MSG_SRVERPCK_NAME = "com.samsung.android.communicationservice";
    /**短信系统短信包名**/
    private static final String MSG_PACKAGENAME = "com.android.mms";
    /**短信 --- vivo Y85A**/
    private static final String SYS_SMS = "com.android.mms.service";
    private static final String XIAOMI_SMS_PACK_NAME = "com.xiaomi.xmsf";


    private static final String SKYPE_PACKAGE_NAME = "com.skype.raider";
    private static final String SKYPE_PACK_NAME = "com.skype.rover";
    /**line**/
    private static final String LINE_PACKAGE_NAME = "jp.naver.line.android";
    private static final String LINE_LITE_PACK_NAME = "com.linecorp.linelite";


    /**谷歌邮箱**/
    private static final String GMAIL_PACKAGE_NAME = "com.google.android.gm";
    /**Snapchat：**/
    private static final String SNAP_PACKAGE_NAME = "com.snapchat.android";

    /**音乐播放器**/
    //酷狗
    private static final String KUGOU_MUSIC_PACK_NAME = "com.kugou.android";
    //QQ音乐
    private static final String QQ_MUISC_PACK_NAME = "com.tencent.qqmusic";
    //网易云
    private static final String WAGNYI_MUSIC_PACK_NAME = "com.netease.cloudmusic";
    //酷我音乐
    private static final String KUWO_MUSIC_PACK_NAME = "cn.kuwo.player";
    //咪咕音乐
    private static final String MIGU_MUSIC_PACK_NAME = "cmccwm.mobilemusic";
    //铃声多的
    private static final String DUODUO_MUSIC_PACK_NAME = "com.shoujiduoduo.ringtone";
    //喜马拉雅
    private static final String XIMALAYA_MUSIC_NAME = "com.ximalaya.ting.android";
    //虾米音乐
    private static final String XIAMI_MUSIC_NAME = "fm.xiami.main";
    //华为音乐
    private static final String HUAWEI_MUSIC_NAME = "com.android.mediacenter";
    //小米音乐
    private static final String XIAOMI_MUSIC_NAME = "com.miui.player";
    //vivo音乐
    private static final String VIVO_MUSIC_NAME = "com.android.bbkmusic";


    public static final HashMap<String,String> notifyMap = new HashMap<>();
    static {
        notifyMap.put(QQ_PACKAGE_NAME,"30");
        notifyMap.put(QQ_FAST_PACK_NAME,"30");
        notifyMap.put(WECHAT_PACKAGE_NAME,"3");
        notifyMap.put(FACEBOOK_PACKAGE_NAME,"4");
        notifyMap.put(FACEBOOK_PACKAGE_NAME1,"30");
        notifyMap.put(TWITTER_PACKAGE_NAME,"30");
        notifyMap.put(WHATS_PACKAGE_NAME,"30");
        notifyMap.put(VIBER_PACKAGE_NAME,"30");
        notifyMap.put(INSTANRAM_PACKAGE_NAME,"30");
        notifyMap.put(SAMSUNG_MSG_PACK_NAME,"30");
        notifyMap.put(SAMSUNG_MSG_SRVERPCK_NAME,"30");
        notifyMap.put(MSG_PACKAGENAME,"30");
        notifyMap.put(SYS_SMS,"30");
        notifyMap.put(XIAOMI_SMS_PACK_NAME,"30");

    }

}
