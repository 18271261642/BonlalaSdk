package com.bonlala.fitalent.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.translationMatrix
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bonlala.fitalent.bean.ExerciseShowBean
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.db.model.ExerciseModel
import timber.log.Timber

/**
 * Created by Admin
 *Date 2022/10/19
 */
class ExerciseViewModel : ViewModel() {

    //查询所有的锻炼数据
    var allExerciseList = MutableLiveData<List<ExerciseShowBean>?>()



    fun queryAllExercise(userId : String, mac: String,type : Int){

        Timber.e("----type="+type)
        val allExercise = DBManager.dbManager.getAllExercise(userId,mac)
        if(allExercise == null){
            allExerciseList.postValue(null)
            return
        }


        val map = HashMap<String,MutableList<ExerciseModel>>()
        allExercise.forEachIndexed { index, exerciseModel ->
            val strDay = exerciseModel.dayStr
            if(map[strDay] != null){
                val lt = map[strDay]
                if(type == -1){
                    lt?.add(exerciseModel)
                    map[strDay] = lt as MutableList<ExerciseModel>
                }else{
                    if(exerciseModel.type == type){
                        lt?.add(exerciseModel)
                        map[strDay] = lt as MutableList<ExerciseModel>
                    }
                }

            }else{
                val tempList = mutableListOf<ExerciseModel>()
                if(type === -1){    //所有的
                    tempList.add(exerciseModel)
                    map[strDay] = tempList
                }else{
                    if(exerciseModel.type == type){
                        tempList.add(exerciseModel)
                        map[strDay] = tempList
                    }
                }

            }
        }

        val resultList = mutableListOf<ExerciseShowBean>()
        map.forEach {
            val exerciseShowBean = ExerciseShowBean()
            exerciseShowBean.exerciseModelList = it.value
            exerciseShowBean.dayStr = it.key
            resultList.add(exerciseShowBean)
        }
        allExerciseList.postValue(resultList)
    }
}