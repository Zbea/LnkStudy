package com.bll.lnkstudy.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.bll.lnkstudy.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageDownLoadUtils {

    /**
     * 图片下载
     */
    private String url;
    private Context context;
    private String typePath;//子类文件夹名称
    private String picName;//图片名称
    private ImageDownLoadCallBack callBack;
    private int type=0;//0作业1考卷
    private File file = null;

    public ImageDownLoadUtils(Context context, String url,String typePath,String picName,int type){
        this.context=context;
        this.url=url;
        this.typePath=typePath;
        this.picName=picName;
        this.type=type;
    }

    public void startDownload(ImageDownLoadCallBack callBack) {
        File file = null;
        Bitmap bitmap = null;

        try {
            bitmap = Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();

            Bitmap finalBitmap = bitmap;
            new Thread(new Runnable() {
                @Override
                public void run() {

                    if (finalBitmap != null){
                        saveBmpGallery(finalBitmap);
                    }
                }

            }).start();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (bitmap != null && file.exists()) {
                callBack.onDownLoadSuccess(bitmap, file.getPath());
            } else {
                callBack.onDownLoadFailed();
            }
        }

    }


    /**
     * 保存bitmap刷新相册
     *
     * @param bmp     获取的bitmap数据
     */
    private void saveBmpGallery(Bitmap bmp) {

        //type为1考卷保存在testPaper目录下
        File imageDir = new File(type==1?Constants.Companion.getTESTPAPER_PATH():Constants.Companion.getHOMEWORK_PATH(),typePath);
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }

        // 声明输出流
        FileOutputStream outStream = null;
        try {
            // 如果有目标文件，直接获得文件对象，否则创建一个以filename为名称的文件
            file = new File(imageDir, picName + ".png");
            // 获得输出流，如果文件中有内容，追加内容
            outStream = new FileOutputStream(file);
            if (null != outStream) {
                bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            }
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, "", "");
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);

    }


    public interface ImageDownLoadCallBack{
        void onDownLoadSuccess(Bitmap bitmap,String path);
        void onDownLoadFailed();
    }


}
