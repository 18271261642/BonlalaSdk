package com.bonlala.fitalent.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Admin
 * Date 2022/10/25
 * @author Admin
 */
public class FileUtils {

    private static final String TAG = "FileUtils";

    public static String savePic(Bitmap b, Context context) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss",
                Locale.US);
        File file = context.getExternalFilesDir(null).getAbsoluteFile();
        // 如果文件不存在，则创建一个新文件
        if (!file.isDirectory()) {
            try {
                file.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String fname = file.getAbsolutePath() + "/" + sdf.format(new Date()) + "_share_order.png";
        File outFile = new File(fname);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outFile);//获取FileOutputStream对象
            if (fos != null) {
                /**
                 * 压缩图片
                 * 第一个参数：要压缩成的图片格式
                 * 第二个参数：压缩率
                 * 第三个参数：压缩到指定位置
                 */
                boolean compress = b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                if (compress) {
                //    Toast.makeText(CompressImageActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    //通知图库更新
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(outFile));
                    context.sendBroadcast(intent);
                } else {
                   // Toast.makeText(CompressImageActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                }
                fos.flush();
                fos.close();//最后关闭此文件输出流并释放与此流相关联的任何系统资源。
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fname;
    }



    /**
     * 支持  ScrollView   ViewGroup  ListView   RecyclerView  NestedScrollView
     * 长截图
     *
     * @param rootView
     * @return
     */

    public static Bitmap getViewBitmap(View rootView) {

        int h = 0;

        Bitmap bitmap;


//        if (rootView instanceof ScrollView) {
//            h = ((ScrollView) rootView).getChildAt(0).getHeight();
//
//            Log.e(TAG, "rootView is ScrollView");
//        } else if (rootView instanceof NestedScrollView) {
//
//            h = ((NestedScrollView) rootView).getChildAt(0).getHeight();
//
//            Log.e(TAG, "rootView is NestedScrollView");
//        } else if (rootView instanceof ListView) {
//            Log.i(TAG, "rootView is ListView");
//            ListView listView = (ListView) rootView;
//            for (int i = 0; i < listView.getChildCount(); i++) {
//
//                h += listView.getChildAt(i).getHeight();
//
//            }
//        } else if (rootView instanceof RecyclerView) {
//            Log.i(TAG, "rootView is RecyclerView");
//            RecyclerView recyclerView = (RecyclerView) rootView;
//            for (int i = 0; i < recyclerView.getChildCount(); i++) {
//
//                h += recyclerView.getChildAt(i).getHeight();
//
//            }
//
//        } else if (rootView instanceof ViewGroup) {
//            Log.i(TAG, "rootView is ViewGroup");
//            h = rootView.getHeight();
//
//        }

        if (rootView instanceof NestedScrollView) {

            h = ((NestedScrollView) rootView).getChildAt(0).getHeight();

            Log.e(TAG, "rootView is NestedScrollView");
        }

        bitmap = Bitmap.createBitmap(rootView.getWidth(), h, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        rootView.draw(canvas);

        return bitmap;

    }




}
