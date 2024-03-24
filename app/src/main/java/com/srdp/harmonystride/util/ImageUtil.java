package com.srdp.harmonystride.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.activity.CertificationActivity;
import com.srdp.harmonystride.activity.ProfileEditActivity;
import com.srdp.harmonystride.entity.User;

import org.litepal.LitePal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class ImageUtil {
    //打开相册请求码
    public static final int OPEN_ALBUM_CODE = 100;
    //打开摄像头请求码
    public static final int OPEN_CAMERA_CODE = 200;
    //图片剪裁请求码
    public static final int PICTURE_CROPPING_CODE = 300;
    //Glide请求图片选项配置
    public static final RequestOptions requestOptionsWithCircle = RequestOptions
            .circleCropTransform()//圆形剪裁
            .diskCacheStrategy(DiskCacheStrategy.NONE);//不做磁盘缓存
    //.skipMemoryCache(true);//不做内存缓存

    //上传成功的回调接口
    public interface OnUploadImage{
        void onUpload(Boolean isUpload);
    }

    public static final int IMAGE_TYPE_USER = 601;
    public static final int IMAGE_TYPE_CERTIFCATION = 602;
    public static final int IMAGE_TYPE_POST = 603;

    public static String USER_PATH = "user/" + SharedPreferenceUtil.getParam("current_account", "").toString() + "/";
    public static String CERTIFICATION_PATH = "certification/" + SharedPreferenceUtil.getParam("current_account", "").toString() + "/";
    public static String POST_PATH = "post/" + SharedPreferenceUtil.getParam("current_account", "").toString() + "/";

    // 将图片资源转换成drawable
    public static Drawable getDrawableFromResourceId(int resourceId) {
        return AppCompatResources.getDrawable(MyApplication.getContext(), resourceId);
    }

    //将图片资源转换成bitmap
    public static Bitmap getBitmapFromResourceId(int resourceId) {
        Drawable drawable = getDrawableFromResourceId(resourceId);
        return ((BitmapDrawable)drawable).getBitmap();
    }

    // 将drawable 图像转化成二进制字节
    public static byte[] drawableToByteArray(Drawable drawable) {
        if (drawable != null) {
            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            // 创建一个字节数组输出流,流的大小为size
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // 设置位图的压缩格式，质量为100%，并放入字节数组输出流中
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
            // 将字节数组输出流转化为字节数组byte[]
            byte[] imagedata = baos.toByteArray();
            return imagedata;
        }
        return null;
    }

    // 将Bitmap转换为字节数组
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // 压缩Bitmap到输出流中
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        // 获取字节数组
        byte[] byteArray = outputStream.toByteArray();
        // 关闭输出流
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    // 获取图片的URI
    public static Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null, null);
        return Uri.parse(path);
    }

    //获取网络图片路径
    public static String getImagePath(String avatarPath){
        //oss Bucket名称
        final String BUCKET_NAME = "harmonystride-bucket";
        final String ENDPOINT = "oss-cn-hangzhou.aliyuncs.com";
        final String ROOT = "img/";
        String IP = "https://" + BUCKET_NAME + "." + ENDPOINT + "/" +ROOT;
        return IP + avatarPath;
    }

    public static String addWaterMark(String url){
        List<User> users = LitePal.where("account = ?", SharedPreferenceUtil.getParam("current_account", "").toString()).find(User.class);
        String nickname = "@" + users.get(0).getNickname();
        byte[] utf8Bytes = nickname.getBytes(StandardCharsets.UTF_8);
        String watermark = URLEncoder.encode(Base64.getEncoder().encodeToString(utf8Bytes));
        String watermarkPath = "?x-oss-process=image/watermark,text_" + watermark + ",t_80,color_6C6C6C,size_20,g_se";
        return url + watermarkPath;
    }

    /**
     * 图片剪裁
     * @param uri 图片uri
     */
    public static void pictureCropping(Context context, Uri uri, int aspectX, int aspectY) {
        // 调用系统中自带的图片剪裁
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", aspectX * 100);
        intent.putExtra("outputY", aspectY * 100);
        //支持缩放
        intent.putExtra("scale", true);
        // 返回裁剪后的数据
        intent.putExtra("return-data", true);

        Activity activity = (Activity) context;
        activity.startActivityForResult(intent, PICTURE_CROPPING_CODE);

    }

    //上传用户头像到OSS
    /**
     * 图片上传
     * @param preImageUrl 待删除的图片URL
     * @param curImage 待上传的图片
     */
    public static void upload(String preImageUrl, Bitmap curImage, String imageUrl, OnUploadImage onUploadImage){
        //删除线程
        Thread deleteImg = new Thread(new Runnable() {
            @Override
            public void run() {
                OSSClientUtil.getInstance().deleteImage(preImageUrl);
            }
        });

        //上传线程
        Thread uploadImg = new Thread(new Runnable() {
            @Override
            public void run() {
                //上传
                OSSClientUtil.getInstance().uploadImage(ImageUtil.bitmapToByteArray(curImage), imageUrl, onUploadImage);
            }
        });

        if(!StringUtil.isEmpty(preImageUrl)){
            deleteImg.start();
            try {
                // 等待第一个线程执行完毕
                deleteImg.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            uploadImg.start();
        }else {
            uploadImg.start();
        }

    }

}
