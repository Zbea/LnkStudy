package com.bll.lnkstudy.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableDecoder;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GlideUtils {


    public static final void setImageUrl(Context mContext,int resId, ImageView imageView){

        RequestOptions requestOptions=new RequestOptions();
        requestOptions.fitCenter();

        Glide.with(mContext)
                .load(resId)
                .apply(requestOptions)
                .into(imageView);

    }

    public static final void setImageUrl(Context mContext,String url, ImageView imageView){

        RequestOptions requestOptions=new RequestOptions();
        requestOptions.fitCenter();

        Glide.with(mContext)
                .load(url)
                .apply(requestOptions)
                .into(imageView);

    }

    /**
     * 设置刷新
     * @param mContext
     * @param url
     * @param imageView
     * @param state
     */
    public static final void setImageUrl(Context mContext,String url, ImageView imageView,int state){

        RequestOptions requestOptions=new RequestOptions();
        requestOptions.fitCenter();

        Glide.with(mContext)
                .load(url)
                .apply(requestOptions)
                .signature(new ObjectKey(url+ state))
                .into(imageView);

    }

    public static final void setImageFile(Context mContext, File file, ImageView imageView){

        RequestOptions requestOptions=new RequestOptions();
        requestOptions.fitCenter();
        Glide.with(mContext)
                .load(file)
                .apply(requestOptions)
                .into(imageView);
    }

    public static final void setImageFileRound(Context mContext, File file, ImageView imageView, int round){

        RequestOptions requestOptions=new RequestOptions();
        requestOptions.fitCenter();
        requestOptions.transform(new RoundedCorners(round));

        Glide.with(mContext)
                .load(file)
                .apply(requestOptions)
                .into(imageView);
    }

    public static void setImageRoundUrl(Context mContext, String url, ImageView imageView, int round){

        RequestOptions requestOptions=new RequestOptions();
        requestOptions.fitCenter();
        requestOptions.transform(new RoundedCorners(round));

        Glide.with(mContext)
                .load(url)
                .apply(requestOptions)
                .into(imageView);
    }

}
