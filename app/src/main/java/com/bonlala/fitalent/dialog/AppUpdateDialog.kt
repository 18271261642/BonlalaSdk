package com.bonlala.fitalent.dialog

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import com.bonlala.fitalent.R
import com.bonlala.fitalent.listeners.OnItemClickListener
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnDownloadListener
import com.hjq.http.model.HttpMethod
import kotlinx.android.synthetic.main.layout_app_update_dialog.*
import timber.log.Timber
import java.io.File

/**
 * app版本更新dialog
 * Created by Admin
 *Date 2022/10/24
 */
class AppUpdateDialog : AppCompatDialog ,View.OnClickListener{

    //下载的目录
    private var downCache : String ?= null
    //版本
    private var version : String?= null

    private var onItemClick : OnItemClickListener ?= null

    fun setOnDownloadListener(onClick : OnItemClickListener){
        this.onItemClick = onClick;
    }

    constructor(context: Context) : super (context){

    }

    constructor(context: Context,theme : Int) : super (context, theme){

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_app_update_dialog)

        downCache = context.getExternalFilesDir(null)?.path
        Timber.e("--down="+downCache)

        appUpdateDialogSkipTv.setOnClickListener(this)
        appUpdateDialogUpdateTv.setOnClickListener(this)

    }


    //是否强制更新
    fun setIsAwayUpdate(isAway : Boolean){
        appUpdateDialogSkipTv.visibility = if(isAway) View.GONE else View.VISIBLE
    }

    //显示更新的版本和内容
    fun showUpdateMsg(versionName : String,versionStr : String){
        appUpdateVersionTv.text = context.resources.getString(R.string.string_last_version)+": v"+versionName
        appUpdateVersionMsgTv.text = context.resources.getString(R.string.string_version_desc)+"\n"+versionStr
        version = versionName
    }

    //开始更新
    fun showStartUpdate(url : String,lifecycleOwner: LifecycleOwner){
        appUpdateProgressView.visibility = View.VISIBLE
        appUpdateDialogUpdateTv.visibility = View.GONE
        appUpdateDialogSkipTv.visibility = View.GONE
        appUpdateProgressView.isDownload = true
        Timber.e("---url="+url+" "+downCache)
        EasyHttp.download(lifecycleOwner).method(HttpMethod.GET).file(downCache+"/"+version+".apk").url(url).listener(object : OnDownloadListener{
            override fun onStart(file: File?) {
                Timber.e("----start="+file?.path)
            }

            override fun onProgress(file: File?, progress: Int) {
                Timber.e("----pro="+progress)
                appUpdateProgressView.setCurrentProgressValue(progress.toFloat(),100f)
                if(progress == 100){
                    installApk()
                }
            }

            override fun onComplete(file: File?) {
                Timber.e("----onComplete="+file?.path)
                installApk()

            }

            override fun onError(file: File?, e: Exception?) {
                Timber.e("----onError="+file?.path+" "+e?.message)
            }

            override fun onEnd(file: File?) {

            }

        }).start()
    }

    override fun onClick(p0: View?) {
        //取消
        if(p0?.id == R.id.appUpdateDialogSkipTv){
            onItemClick?.onIteClick(0x00)
        }

        //更新
        if(p0?.id == R.id.appUpdateDialogUpdateTv){
            onItemClick?.onIteClick(0x01)
        }
    }


    //安装
    private fun installApk(){
        val file: File = File(downCache+"/"+version+".apk")
        if (!file.isFile) return
        if (file.name != version+".apk") return
        context.startActivity(getInstallIntent())
    }

    /**
     * 获取安装意图
     */
    private fun getInstallIntent(): Intent {
        val file: File = File(downCache+"/"+version+".apk")
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        val uri: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        } else {
            uri = Uri.fromFile(file)
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }
}