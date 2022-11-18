package com.bonlala.fitalent.emu;

import android.content.Context;

import com.bonlala.fitalent.R;

import java.util.HashMap;
import java.util.Map;

/**
 * W560B的锻炼类型
 * Created by Admin
 * Date 2022/10/20
 * @author Admin
 */
public class W560BExerciseType {



    /**走路**/
    public static final int TYPE_WALK = 0x01;

    /**跑步**/
    public static final int TYPE_RUN = 0x02;

    /**骑行**/
    public static final int TYPE_RIDE = 0x03;

    /**登山**/
    public static final int TYPE_MOUNTAINEERING = 0x08;

    /**足球**/
    public static final int TYPE_FOOTBALL = 0x07;

    /**篮球**/
    public static final int TYPE_BASKETBALL = 0x06;

    /**乒乓球**/
    public static final int TYPE_PINGPONG = 0x09;

    /**羽毛球**/
    public static final int  TYPE_BADMINTON= 0x05;


    private final static Map<Integer,String> map = new HashMap<>();


    /**
     * 获取W560B的锻炼名称
     * @param type 类型
     * @param context context
     * @return
     */
    public static String getW560BTypeName(int type, Context context){
        if(type == -1){
            return context.getResources().getString(R.string.string_exercise_record);
        }
        map.clear();
        map.put(TYPE_WALK,context.getResources().getString(R.string.string_exericse_walk));
        map.put(TYPE_RUN,context.getResources().getString(R.string.string_exercise_run));
        map.put(TYPE_RIDE,context.getResources().getString(R.string.string_exercise_ride));
        map.put(TYPE_MOUNTAINEERING,context.getResources().getString(R.string.string_exercise_mountaineering));

        map.put(TYPE_FOOTBALL,context.getResources().getString(R.string.string_exercise_football));
        map.put(TYPE_BASKETBALL,context.getResources().getString(R.string.string_exercise_basketball));
        map.put(TYPE_PINGPONG,context.getResources().getString(R.string.string_exercise_pingpang));
        map.put(TYPE_BADMINTON,context.getResources().getString(R.string.string_exercise_badminton));

        return map.get(type);


    }


    public static int getTypeResource(int type){
        if(type == W560BExerciseType.TYPE_WALK){
            return R.mipmap.ic_sport_walk;
        }
        if(type == W560BExerciseType.TYPE_RUN) {
            return R.mipmap.ic_sport_run;
        }
        if(type == W560BExerciseType.TYPE_RIDE) {
            return R.mipmap.ic_sport_ride;
        }
        if(type == W560BExerciseType.TYPE_MOUNTAINEERING){
            return R.mipmap.ic_sport_mountaineering;
        }

        if(type == W560BExerciseType.TYPE_FOOTBALL){
            return R.mipmap.ic_sport_football;
        }

        if(type == W560BExerciseType.TYPE_BASKETBALL){
            return R.mipmap.ic_sport_basketball;
        }

        if(type == W560BExerciseType.TYPE_PINGPONG){
            return R.mipmap.ic_sport_pingpong;
        }

        if(type == W560BExerciseType.TYPE_BADMINTON){
            return R.mipmap.ic_sport_badmination;
        }

        return R.mipmap.ic_sport_walk;
    }
}
