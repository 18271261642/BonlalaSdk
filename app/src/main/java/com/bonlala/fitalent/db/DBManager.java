package com.bonlala.fitalent.db;

import android.database.sqlite.SQLiteDatabase;

import com.bonlala.fitalent.db.model.CommDbTimeModel;
import com.bonlala.fitalent.db.model.DataRecordModel;
import com.bonlala.fitalent.db.model.DeviceSetModel;
import com.bonlala.fitalent.db.model.ExerciseModel;
import com.bonlala.fitalent.db.model.OneDayHeartModel;
import com.bonlala.fitalent.db.model.OneDayStepModel;
import com.bonlala.fitalent.db.model.SingleBpModel;
import com.bonlala.fitalent.db.model.SingleHeartModel;
import com.bonlala.fitalent.db.model.SingleSpo2Model;
import com.bonlala.fitalent.db.model.SleepModel;
import com.bonlala.fitalent.db.model.StepItem;
import com.bonlala.fitalent.db.model.SumSportModel;
import com.bonlala.fitalent.db.model.UserInfoModel;
import com.bonlala.fitalent.emu.DbType;
import com.bonlala.fitalent.utils.BikeUtils;
import com.google.gson.Gson;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import timber.log.Timber;

/**
 * 数据库操作的管理
 * Created by Admin
 * Date 2022/8/8
 * @author Admin
 */
public class DBManager {

    public static volatile DBManager dbManager = null;

    private final Gson gson = new Gson();

    private static final String commWhere = "userId = ? and deviceMac = ? and saveDay = ?";

    public synchronized static DBManager getInstance(){
        synchronized (DBManager.class){
            if(dbManager == null){
                dbManager = new DBManager();
            }
        }
        return dbManager;
    }

    private  DBManager() {
    }


    /**
     *  初始化用户数据，app初次打开是初始化默认值
     */
    public void initUserInfoData(){
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setUserId("user_1001");
        userInfoModel.setUserUnit(0);
        userInfoModel.setUserHeight(180);
        userInfoModel.setUserWeight(80);
        userInfoModel.setDistanceGoal(5000);
        userInfoModel.setKcalGoal(1000);
        userInfoModel.setStepGoal(8000);
        userInfoModel.setNickName("nickName");
        userInfoModel.setSex(0);
        userInfoModel.setUserBirthday("1990-01-01");
        if(getUserInfo() == null){
            userInfoModel.save();
        }
    }



    //修改用户数据
    public  void updateUserInfo(UserInfoModel userInfoModel){
        String where = "userId=?";
        userInfoModel.saveOrUpdate(where,userInfoModel.getUserId());
    }


    /**
     * 获取用户绑定的Mac
     * @return
     */
    public static String getBindMac(){
        UserInfoModel userInfoModel = getUserInfo();
        if(userInfoModel == null){
            return null;
        }
        return userInfoModel.getUserBindMac();
    }

    /**
     * 获取用户绑定的设备类型，未绑定为0
     * @return
     */
    public static int getBindDeviceType(){
        UserInfoModel  userInfoModel = getUserInfo();
        if(userInfoModel == null){
            return 0;
        }
        return userInfoModel.getUserBindDeviceType();
    }

    //查询用户的信息，单机版实际上就只有一条用户信息
    public static UserInfoModel getUserInfo(){
        try {
            List<UserInfoModel> userInfoModelList = LitePal.findAll(UserInfoModel.class);
            if(userInfoModelList == null || userInfoModelList.isEmpty())
                return null;
            return userInfoModelList.get(0);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }





    public boolean saveDeviceD(){
        DeviceSetModel deviceSetModel = new DeviceSetModel();
        deviceSetModel.setDeviceMac("dd");
        deviceSetModel.setUserId("cccc");
        boolean save = deviceSetModel.save();

        Timber.e("-----save="+save);
        return save;
    }


    //查询所有设置数据
    public List<DeviceSetModel> getAllDeviceSet(){
        return LitePal.findAll(DeviceSetModel.class);
    }


    //保存设置相关
    public  boolean saveDeviceSetData(String userId,String mac,DeviceSetModel deviceSetModel){
        try {
            //执行该方法后，表就会被新建出来
            SQLiteDatabase db = Connector.getDatabase();
            deviceSetModel.setUserId(userId);
            deviceSetModel.setDeviceMac(mac);
            //先查询是否存在，存在就修改否保存
            DeviceSetModel saveD = getDeviceSetModel(userId,mac);
            Timber.e("---------是否有存储="+(deviceSetModel == null)+(saveD == null)+" "+new Gson().toJson(deviceSetModel));
            if(saveD == null){
                assert deviceSetModel != null;
                boolean isSave = deviceSetModel.save();
                return isSave;
            }else{
                return deviceSetModel.saveOrUpdate("userId = ? and deviceMac = ?",userId,mac);
            }

           // deviceSetModel.saveOrUpdate(commWhere,userId,mac,day);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    //查询设置相关，只有一条数据
    public  DeviceSetModel getDeviceSetModel(String userId,String mac){
        Timber.e("---useid="+userId+" "+mac);
        try {
            List<DeviceSetModel> deviceSetModelList = LitePal.where("userId = ? and deviceMac = ?",userId,mac).find(DeviceSetModel.class);
            return deviceSetModelList == null || deviceSetModelList.isEmpty() ? null : deviceSetModelList.get(0);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**保存测量的心率数据，**/
    public  void saveMeasureHeartData(String userId,String mac,long time,int ht){
        if(ht == 255)
            return;
        SingleHeartModel singleHeartModel = new SingleHeartModel();
        singleHeartModel.setDeviceMac(mac);
        singleHeartModel.setSaveLongTime(time);
        singleHeartModel.setUserId(userId);
        singleHeartModel.setHeartValue(ht);
        singleHeartModel.setDayStr(BikeUtils.getFormatDate(time,"yyyy-MM-dd"));
        singleHeartModel.save();
    }

    //查询测量的心率数据
    public  List<SingleHeartModel> querySingleHeart(String userId,String mac){
        try {
            List<SingleHeartModel> list = LitePal.where("userId = ? and deviceMac = ?",userId,mac).find(SingleHeartModel.class);
            return list == null || list.isEmpty() ? null : list;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    //查询测量的血压
    public List<SingleBpModel> querySingleBp(String userId,String mac){
        try {
            List<SingleBpModel> list = LitePal.where("userId = ? and deviceMac = ?",userId,mac).find(SingleBpModel.class);
            return list == null || list.isEmpty() ? null : list;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**保存测量的血压数据**/
    public  void saveMeasureBp(String user,String mac,long time,int sBp,int disBp){
        if(sBp == 255 || disBp == 255)
            return;
        SingleBpModel singleBpModel = new SingleBpModel();
        singleBpModel.setDayStr(BikeUtils.getFormatDate(time,"yyyy-MM-dd"));
        singleBpModel.setDeviceMac(mac);
        singleBpModel.setSaveLongTime(time);
        singleBpModel.setUserId(user);
        singleBpModel.setDiastolicBp(disBp);
        singleBpModel.setSysBp(sBp);
        singleBpModel.save();
    }


    //查询血压
    public List<SingleSpo2Model> querySingleSpo2(String userId,String mac){
        try {
            List<SingleSpo2Model> list = LitePal.where("userId = ? and deviceMac = ?",userId,mac).find(SingleSpo2Model.class);
            return list == null || list.isEmpty() ? null : list;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    //保存测量的血氧
    public  void saveMeasureSpo2(String userId,String mac,long time,int spo2){
        if(spo2 == 255)
            return;
        SingleSpo2Model singleSpo2Model = new SingleSpo2Model();
        singleSpo2Model.setUserId(userId);
        singleSpo2Model.setDeviceMac(mac);
        singleSpo2Model.setDayStr(BikeUtils.getFormatDate(time,"yyyy-MM-dd"));
        singleSpo2Model.setSpo2Value(spo2);
        singleSpo2Model.setSaveLongTime(time);
        singleSpo2Model.save();
    }

    //查询保存的血氧数据
    public static List<SingleSpo2Model> querySingleSpo2Value(String userId,String mac){
        try {
           List<SingleSpo2Model> list = LitePal.where("userId = ? and deviceMac = ?",userId,mac).find(SingleSpo2Model.class);
           return list == null || list.isEmpty() ? null : list;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    //保存当日的总步数，替换，每天只有一条
    public static void saveTodayCountSport(String userId,String bleName,String mac,long time,int step,int distance,int kacl){
       try {
           String where = "userId = ? and deviceMac = ? and saveDay = ?";
           SumSportModel sumSportModel = new SumSportModel();
           sumSportModel.setSaveLongTime(time);
           sumSportModel.setSumStep(step);
           sumSportModel.setSumKcal(kacl);
           sumSportModel.setSumDistance(distance);

           sumSportModel.setSaveDay(BikeUtils.getFormatDate(time,"yyyy-MM-dd"));
           sumSportModel.setDeviceId(mac);
           sumSportModel.setDeviceName(bleName);
           sumSportModel.setDeviceMac(mac);
           sumSportModel.setUserId(userId);

           sumSportModel.saveOrUpdate(where,userId,mac,BikeUtils.getFormatDate(time,"yyyy-MM-dd"));
       }catch (Exception e){
           e.printStackTrace();
       }
    }


    //查询当前的总步数
    public static SumSportModel getCurrentDayModel(String userId,String mac){
        try {
            List<SumSportModel> sumSportModelList = LitePal.where(commWhere,userId,mac,BikeUtils.getCurrDate()).find(SumSportModel.class);
            if(sumSportModelList == null || sumSportModelList.size() == 0)
                return null;
            return sumSportModelList.get(0);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    //查询一天的详细计步是否存在
    public OneDayStepModel hasOnDayDetailStep(String userId,String mac,String dayStr){
        try {
            List<OneDayStepModel> oneDayStepModels = LitePal.where("userId = ? and deviceMac = ? and dayStr = ?",userId,mac,dayStr).find(OneDayStepModel.class);

            return oneDayStepModels == null || oneDayStepModels.isEmpty() ? null : oneDayStepModels.get(0);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 根据开始和结束日期查询数据计步数据
     * @param userId uid
     * @param mac mac
     * @param startTime 开始日期 yyyy-MM-dd
     * @param endTime 结束日期 yyyy-MM-dd
     * @return 数据
     */
    public List<OneDayStepModel> getStartAndEndTimeStepData(String userId,String mac,String startTime,String endTime){
        try {
            List<OneDayStepModel> oneDayStepModelList = LitePal.where("userId = ? and deviceMac = ? and dayStr between ? and ?",userId,mac,startTime,endTime).find(OneDayStepModel.class);

            return oneDayStepModelList == null || oneDayStepModelList.isEmpty() ? null : oneDayStepModelList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 保存当日的详细计步数据，一天一条，替换
     * @param userId uId
     * @param bleName bName
     * @param mac mac
     * @param dayStr 日期yyyy-MM-dd 对应手表存储的数据，比较今天或昨天，
     * @param stepList 计步集合，最大1440，不够补0
     */
    public  void saveCurrentDayDetailStep(String userId,String bleName,String mac,String dayStr,int step,int calories,int distance,List<Integer> stepList){
        try {
            OneDayStepModel oneDayStepModel = new OneDayStepModel();
            oneDayStepModel.setUserId(userId);
            oneDayStepModel.setDeviceMac(mac);
            oneDayStepModel.setDeviceName(bleName);
            oneDayStepModel.setDayStr(dayStr);
            oneDayStepModel.setDayStep(step);
            oneDayStepModel.setDayCalories(calories);
            oneDayStepModel.setDayDistance(distance);
            Timber.e("-stepList="+stepList.size());
            List<Integer> tempList = new ArrayList<>();
            tempList.addAll(stepList);
            if(stepList.size()<1440){
                int length = 1440-stepList.size();
                for(int i = 0;i<length;i++){
                    tempList.add(0);
                }
            }

            /**
             * 判断计步是否有数据，如果最大的步数为0，就表示无数据
             */
            if(Collections.max(tempList) == 0){
                return;
            }

            int[] stepArray = new int[tempList.size()];
            Timber.e("----222---长度="+tempList.size());
            for(int i = 0;i<tempList.size();i++){
                stepArray[i] = tempList.get(i);
            }

            List<StepItem> detailLite = new ArrayList<>();

            int countStep = 0;
            //将每分钟的转换成每小时的
            List<int[]> itemList = new ArrayList<>();

            for(int i = 0;i<stepArray.length;i+=60){
                int[] itemArr = new int[60];
                if(i+60 <=stepArray.length){
                    System.arraycopy(stepArray,i,itemArr,0,59);
                    itemList.add(itemArr);
                }
            }


            for(int i = 0;i<itemList.size();i++){
                countStep = 0;
                int[] tempItem = itemList.get(i);
                for(int v : tempItem){
                    countStep +=v;
                }
                StepItem stepItem = new StepItem(countStep,i);
                detailLite.add(stepItem);
            }
//
            oneDayStepModel.setDetailStep(new Gson().toJson(detailLite));
            Timber.e("------处理完整了="+new Gson().toJson(oneDayStepModel));
            boolean isSave;
            if(hasOnDayDetailStep(userId,mac,dayStr) != null){
                isSave = oneDayStepModel.saveOrUpdate("userId = ? and deviceMac = ? and dayStr=?",userId,mac,dayStr);
            }else{
                isSave = oneDayStepModel.save();
            }
            Timber.e("----计步="+isSave+" "+dayStr);

            saveDateTypeDay(userId,mac,dayStr, DbType.DB_TYPE_STEP);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**查询一整天的心率数据**/
    public OneDayHeartModel queryOnDayHeart(String userId,String mac,String dayStr){
        try {
            List<OneDayHeartModel> list = LitePal.where("userId = ? and deviceMac = ? and dayStr = ?",userId,mac,dayStr).find(OneDayHeartModel.class);
            return list == null || list.size() == 0 ? null : list.get(0);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询前10条详细心率数据
     */
    public OneDayHeartModel queryLastTenHr(String userId,String mac){
        try {
            List<OneDayHeartModel> tenHrList = LitePal.where("userId = ? and deviceMac = ?",userId,mac).limit(10).find(OneDayHeartModel.class);
            return tenHrList == null || tenHrList.isEmpty() ? null : tenHrList.get(tenHrList.size()-1);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }



    /**
     * 保存一整天的心率数据，一整天1440个，每分钟一个，不够补0
     * @param userId uId
     * @param bleName bName
     * @param mac mac
     * @param dayStr yyyy-MM-dd
     * @param htList 心率集合
     */
    public void saveOnDayHeartData(String userId,String bleName,String mac,String dayStr,List<Integer> htList){
        OneDayHeartModel oneDayHeartModel = new OneDayHeartModel();
        oneDayHeartModel.setUserId(userId);
        oneDayHeartModel.setDayStr(dayStr);
        oneDayHeartModel.setDeviceName(bleName);
        oneDayHeartModel.setDeviceMac(mac);

        oneDayHeartModel.setSaveLongTime(System.currentTimeMillis());

        List<Integer> tempHtList = new ArrayList<>();
        tempHtList.addAll(htList);
        if(tempHtList.size()<1440){
            int length = 1440 - tempHtList.size();
            for(int i = 0;i<length;i++){
                tempHtList.add(0);
            }
        }

        /**
         * 判断是否是正常的心率
         */
        int maxHr = Collections.max(tempHtList);
        int minHr = Collections.min(tempHtList);
        if(maxHr == 0 && minHr == 0){

            return;
        }

        oneDayHeartModel.setHeartList(tempHtList);

        OneDayHeartModel saveHt = queryOnDayHeart(userId,mac,dayStr);
        boolean isSaveHeart = false;
        if(saveHt == null){
            isSaveHeart = oneDayHeartModel.save();
        }else{
            isSaveHeart = oneDayHeartModel.saveOrUpdate("userId = ? and deviceMac = ? and dayStr = ?",userId,mac,dayStr);
        }

        Timber.e("-----存储心率="+isSaveHeart);

        saveDateTypeDay(userId,mac,dayStr, DbType.DB_TYPE_DETAIL_HR);
    }


    //保存睡眠，
    public void saveOnDaySleepData(String userId,String bleName,String mac,String day,List<Integer> morningList,List<Integer> nightList){
        SleepModel sleepModel = new SleepModel();
        sleepModel.setSaveDay(day);
        sleepModel.setDeviceMac(mac);
        sleepModel.setDeviceName(bleName);
        sleepModel.setUserId(userId);


        Timber.e("----晚上睡眠="+nightList.size()+" "+new Gson().toJson(nightList));
        //不为0的开始计算入睡时间
        List<Integer> resultMorningList = new ArrayList<>();

        List<Integer> resultNightList = new ArrayList<>();

        //开始的下标
        int startIndex = 0;
        if(morningList.size()<480){
            resultMorningList.addAll(morningList);
            int length = 480-morningList.size();
            for(int i = 0;i<length;i++){
                resultMorningList.add(2);
            }
        }else{
            resultMorningList.addAll(morningList);
        }


        //晚上8点到24点的睡眠
        if(nightList.size()<240){
            resultNightList.addAll(nightList);
            int length = 240 - nightList.size();
            for(int i = 0;i<length;i++)
                resultNightList.add(2);
        }else{
            resultNightList.addAll(nightList);
        }

        /**
         * 判断是否有正常的睡眠数据
         * 如果睡眠数组中最大和最小值均为2，则表明没有睡眠数据
         */
        int morningMax = Collections.max(resultMorningList);
        int morningMin = Collections.min(resultMorningList);
        int nightMax = Collections.max(resultNightList);
        int nightMin = Collections.min(resultNightList);
        if(morningMax == 2 && morningMin == 2 && nightMax == 2 && nightMin == 2){

            return;
        }


        sleepModel.setMorningSleepStr(gson.toJson(resultMorningList));
        sleepModel.setNightSleepStr(gson.toJson(resultNightList));
        SleepModel saveSleep = getDaySleepData(userId,mac,day);
        boolean isSaveSleep = false;
        if(saveSleep == null){
            isSaveSleep = sleepModel.save();
        }else{
            isSaveSleep = sleepModel.saveOrUpdate("userId = ? and deviceMac = ? and saveDay = ?",userId,mac,day);
        }

        Timber.e("---isSaveSleep="+isSaveSleep);
        if(morningMax == 2 && morningMin == 2){

            return;
        }
        saveDateTypeDay(userId,mac,day, DbType.DB_TYPE_SLEEP);
    }


    /**
     *  查询某天的睡眠是否存在
     *  day yyyy-MM-dd格式
     */

    public SleepModel getDaySleepData(String userId,String mac,String day){
        try {
            List<SleepModel> sleepModelList = LitePal.where("userId = ? and deviceMac = ? and saveDay = ?",userId,mac,day).find(SleepModel.class);
            return sleepModelList == null || sleepModelList.size()==0 ? null : sleepModelList.get(sleepModelList.size()-1);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 存储数据类型的日期 eg:心率 2000-01-01有数据就存个日期
     */
    public void saveDateTypeDay(String userId,String mac,String day,int type){
        DataRecordModel dataRecordModel = new DataRecordModel();
        dataRecordModel.setDataType(type);
        dataRecordModel.setDayStr(day);
        dataRecordModel.setDeviceMac(mac);
        dataRecordModel.setUserId(userId);
        dataRecordModel.setSaveLongTime(System.currentTimeMillis());
        boolean isSave = isSavedForDataOfDay(userId,mac,day,type);
        boolean saveStatus = false;
        if(isSave){
            saveStatus = dataRecordModel.saveOrUpdate("userId = ? and deviceMac = ? and dayStr = ? and dataType = ?",userId,mac,day,String.valueOf(type));
        }else{
            saveStatus = dataRecordModel.save();
        }
        Timber.e("-------save="+saveStatus);
    }

    /**
     * 查询存储的类型日期
     */
    public boolean isSavedForDataOfDay(String userId,String mac,String day,int type){
        List<DataRecordModel> dataRecordModelList = LitePal.where("userId = ? and deviceMac = ? and dayStr = ? and dataType = ?",userId,mac,day, String.valueOf(type)).find(DataRecordModel.class);
        return dataRecordModelList != null && dataRecordModelList.size()>0;
    }


    /**
     * 查询最近一次类型有数据的日期
     */

    public String getLastDayOfType(String userId,String mac,int type){
        try {
            List<DataRecordModel> list = LitePal.where("userId = ? and deviceMac = ? and dataType = ?",userId,mac, String.valueOf(type)).find(DataRecordModel.class);
             if(list == null || list.isEmpty())
                 return null;
            Timber.e("-----锻炼="+new Gson().toJson(list));
            Collections.sort(list, new Comparator<DataRecordModel>() {
                @Override
                public int compare(DataRecordModel dataRecordModel, DataRecordModel t1) {
                    return t1.getDayStr().compareTo(dataRecordModel.getDayStr());
                }
            });
           // Timber.e("-----dddd="+new Gson().toJson(list.get(0).getDayStr()));
            String dayStr = list.get(0).getDayStr();
            int[] dayArray = BikeUtils.getDayArrayOfStr(dayStr);
            if(dayArray[1] == 0 || dayArray[2] >31){
                if(list.size()>1){
                    return list.get(1).getDayStr();
                }
                return null;
            }
           return list.get(0).getDayStr();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取所的记录
     * @param userId uId
     * @param mac mac
     * @param type 类型
     * @return
     */
    public List<String> getAllRecordByType(String userId,String mac,int type){
        if(mac == null)
            return null;
        List<String> dateList = new ArrayList<>();
        try {
            List<DataRecordModel> list = LitePal.where("userId = ? and deviceMac = ? and dataType = ?",userId,mac, String.valueOf(type)).find(DataRecordModel.class);
            if(list == null || list.isEmpty())
                return null;
            for(DataRecordModel dataRecordModel : list){
                dateList.add(dataRecordModel.getDayStr());
            }
            return dateList;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 保存锻炼数据
     */
    public void saveExerciseData(String userId, String mac, String startTime, ExerciseModel eb){
       eb.setUserId(userId);
       eb.setDeviceMac(mac);
       eb.setSaveTime(System.currentTimeMillis());

       ExerciseModel exerciseModel = getExerciseByTime(userId,mac,startTime);
       boolean isSave = false;
       if(exerciseModel == null){
           isSave = eb.save();
       }else{
           isSave =  eb.saveOrUpdate("userId = ? and deviceMac = ? and startTime = ?",userId,mac,startTime);
       }
        Timber.e("------出没出锻炼数据="+isSave);
       DBManager.dbManager.saveDateTypeDay(userId,mac,eb.getDayStr(),DbType.DB_TYPE_EXERCISE);
    }

    /**查询是否有对应某条的锻炼数据**/
    public ExerciseModel getExerciseByTime(String userId,String mac,String startTime){
        try {
            List<ExerciseModel> exerciseModelList = LitePal.where("userId = ? and deviceMac = ? and startTime = ?",userId,mac,startTime).find(ExerciseModel.class);
            return exerciseModelList == null || exerciseModelList.isEmpty() ? null : exerciseModelList.get(0);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询天的锻炼数据
     */
    public List<ExerciseModel> getDayExercise(String userId,String mac,String day){
        try {
            List<ExerciseModel> exerciseModelList = LitePal.where("userId = ? and deviceMac = ? and dayStr = ?",userId,mac,day).find(ExerciseModel.class);
            return exerciseModelList == null || exerciseModelList.isEmpty() ? null : exerciseModelList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }



    /**
     * 查询所有的锻炼数据
     */
    public List<ExerciseModel> getAllExercise(String userId,String mac){
        try {
            List<ExerciseModel> exerciseModelList = LitePal.where("userId = ? and deviceMac = ? ",userId,mac).find(ExerciseModel.class);
            return exerciseModelList == null || exerciseModelList.isEmpty() ? null : exerciseModelList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除锻炼数据
     */
    public  void deleteExercise(String userId,String mac,String startTime){
        try {
            LitePal.deleteAll(ExerciseModel.class,"userId = ? and deviceMac = ? and startTime = ?",userId,mac,startTime);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 保存不同类型的提醒
     * @param userId userId
     * @param mac mac
     * @param type 类型
     * @param commDbTimeModel 数据
     */
    public void saveDeviceNotifyForType(String userId, String mac, String type, CommDbTimeModel commDbTimeModel){
        CommDbTimeModel saveDb = getDbNotifyType(userId,mac,type);
        CommDbTimeModel db = new CommDbTimeModel();
        db.setUserId(userId);
        db.setDeviceMac(mac);
        db.setDbType(type);
        db.setStartHour(commDbTimeModel.getStartHour());
        db.setStartMinute(commDbTimeModel.getStartMinute());
        db.setEndHour(commDbTimeModel.getEndHour());
        db.setEndMinute(commDbTimeModel.getEndMinute());
        db.setLevel(commDbTimeModel.getLevel());
        db.setSwitchStatus(commDbTimeModel.getSwitchStatus());

        boolean isSave = false;

        if(saveDb == null){
            isSave =  db.save();
        }else{
            isSave = db.saveOrUpdate("userId = ? and deviceMac = ? and dbType = ?",userId,mac,type);
        }
        Timber.e("-----isSave="+isSave);
    }


    /**
     * 获取设备提醒的数据
     * @param userId ueserId
     * @param mac mac
     * @param type 类型
     * @return 是否有数据
     */
    public CommDbTimeModel getDbNotifyType(String userId, String mac, String type ){
        try {
            List<CommDbTimeModel> dbTimeModelList = LitePal.where("userId = ? and deviceMac = ? and dbType = ?",userId,mac,type).find(CommDbTimeModel.class);
            return dbTimeModelList == null || dbTimeModelList.isEmpty() ? null : dbTimeModelList.get(0);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
