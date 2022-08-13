package com.bll.lnkstudy.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableDecoder;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class GlideUtils {



    public static final void setImageUrl(Context mContext,String url, ImageView imageView){
        Glide.with(mContext)
                .load(url)
                .into(imageView);
    }

    public static final void setImageFile(Context mContext, File file, ImageView imageView){
        Glide.with(mContext)
                .load(file)
                .into(imageView);
    }

    public static final void setImageRoundUrl(Context mContext,String url, ImageView imageView,int round){

        RequestOptions requestOptions=new RequestOptions();
        requestOptions.fitCenter();
        requestOptions.transform(new RoundedCorners(round));
        requestOptions.skipMemoryCache(false);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.DATA);

        Glide.with(mContext)
                .load(url)
                .apply(requestOptions)
                .into(imageView);
    }

    public static final void setImageNoCacheUrl(Context mContext,String url, ImageView imageView){

        RequestOptions requestOptions=new RequestOptions();
        requestOptions.fitCenter();
        requestOptions.skipMemoryCache(true);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        Glide.with(mContext)
                .load(url)
                .apply(requestOptions)
                .into(imageView);
    }

    public static final Bitmap getBitmap(Context mContext,String url){
        Bitmap bitmap=null;
        try {
            bitmap=Glide.with(mContext)
                    .asBitmap()
                    .load(url)
                    .into(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL)
                    .get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
