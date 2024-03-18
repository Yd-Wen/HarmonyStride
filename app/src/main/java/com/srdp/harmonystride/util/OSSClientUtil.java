package com.srdp.harmonystride.util;


import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.common.utils.DateUtil;
import com.alibaba.sdk.android.oss.model.DeleteObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.entity.Result;
import com.srdp.harmonystride.entity.User;

import org.litepal.LitePal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OSSClientUtil {
//    private static interface onUpload{
//
//    }

    private static final String TAG = "OSSClientUtil";
    //oss访问地址
    private static final String ENDPOINT = "oss-cn-hangzhou.aliyuncs.com";
    //oss账号的AccessKey ID
    private static String ACCESS_KEY_ID = "STS.NTtatrYHb2JPoCX5FeRq3VvNL";
    //oss账号的AccessKey Secret
    private static String ACCESS_KEY_SECRET = "2ibHwtugTQDeK55o5r1u4EYFcA37YjuDDpy4i5mWYbdy";
    //oss Bucket名称
    private static final String BUCKET_NAME = "harmonystride-bucket";
    private static final String ROOT = "img/";
    // 从STS服务获取的安全令牌（SecurityToken）
    private static String SECURITY_TOKEN= "CAISoAJ1q6Ft5B2yfSjIr5fBKs7GtJdDhYi7bWXp0UYwXv4fubPlrjz2IHhMf3hsAesatPs3mW1R7f8flqJIRoReREvCUcZr8syyGLYy0NOT1fau5Jko1beiewHKeRyZsebWZ+LmNqS/Ht6md1HDkAJq3LL+bk/Mdle5MJqP+/EFA9MMRVv6FxIkYu1bPQx/ssQXGGLMPPK2SH7Qj3HXEVBjt3gw6yl24r/txdaHuFiMzg/6w/UQoILgOYS+bsVheZ1iWNS4wKs0VNKYjXABsUYWrfYs1fUeom+bhLzHXQkNuSfhGvHP79hiIDV+YqUHAKNepJD+76Yg4reCy9ysl0wSZbwICn+GGZrZwMLeHeSvJ80lb7f3NmjAycyf7nx2Dqer7RgagAEK6wpnIPfYdVuCzwYn/Vf2OdHj7zaFyyygVlNEmq1UzAdnUlxp3YK2VeLa46OTybQqcHEK9jR/b4+3bKj5Kgo4fxVE2kMgLi8CvukTtZQoOYY1Fh7U7M/twu454v3qtSAtMzF45NLKvDI4NV9xrwtGWIELZU0JLlhf8lLjaoz8BiAA";
    //凭证过期时间
    private static String EXPIRATION = "2024-02-19T03:44:40Z";
    //获取凭证
    OSSCredentialProvider credentialProvider = new OSSFederationCredentialProvider() {
        @Override
        public OSSFederationToken getFederationToken() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                Date date = sdf.parse(EXPIRATION);
                if(date != null){
                    long expiration = date.getTime() / 1000;
                    // 如果StsToken即将过期，有效时间小于5分钟，则更新StsToken。
                    if (DateUtil.getFixedSkewedTimeMillis() / 1000 > expiration - 5 * 60) {
                        LogUtil.d("update stsCredential", "reqesting");

                       OkHttpClient client = new OkHttpClient();

                        Request request = new Request.Builder()
                                .url(HTTPUtil.IP + "/sts/update")
                                .build();

                        try {
                            Response response = client.newCall(request).execute();
                            String responseBody = response.body().string();
                            Result result = new Gson().fromJson(responseBody, Result.class);
                            if (result.getCode() == 1) {
                                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                                JsonObject data = jsonObject.getAsJsonObject("data");
                                //注意不要用toString，否则引号会保留到字符串
                                ACCESS_KEY_ID = data.get("accessKeyId").getAsString();
                                ACCESS_KEY_SECRET = data.get("accessKeySecret").getAsString();
                                SECURITY_TOKEN = data.get("securityToken").getAsString();
                                EXPIRATION = data.get("expiration").getAsString();
                                LogUtil.d("update stsCredential", "update success");
                            } else {
                                LogUtil.e("update stsCredential", "update error");
                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return new OSSFederationToken(ACCESS_KEY_ID, ACCESS_KEY_SECRET, SECURITY_TOKEN, EXPIRATION);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }
    };

    private static OSSClientUtil instance = null;

    private static OSSClient ossClient = null;

    public static OSSClientUtil getInstance(){
        if (instance == null) {
            synchronized (OSSClientUtil.class) {
                if (instance == null)
                    instance = new OSSClientUtil();
            }
        }
        return instance;
    }

    public OSSClientUtil() {
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog(); // 开启日志
        // 初始化OSSClient
        ossClient = new OSSClient(MyApplication.getContext(), ENDPOINT, credentialProvider);
    }

    public static void uploadImage(byte[] imageData, String subPath, ImageUtil.OnUploadImage onUploadImage){
        // 生成object key
        String objectKey = ROOT + subPath;

        // 设置上传参数
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageData.length);
        metadata.setContentType("image/png");

        PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, objectKey, imageData, metadata);

        try {
            PutObjectResult putResult = ossClient.putObject(putObjectRequest);
            if(putResult.getStatusCode() == 200 && onUploadImage != null){
                onUploadImage.onUpload(true);
            }
            LogUtil.d(TAG, "uploadImage: " + putResult.getETag());
            LogUtil.d("result", putResult.toString());
        } catch (ClientException e) {
            // 客户端异常，例如网络异常等
            e.printStackTrace();
        } catch (ServiceException e) {
            // 服务端异常
            Log.e("RequestId", e.getRequestId());
            Log.e("ErrorCode", e.getErrorCode());
            Log.e("HostId", e.getHostId());
            Log.e("RawMessage", e.getRawMessage());
        }


    }

    public static byte[] downloadImage(String imageUrl) {

        // 生成object key
        String objectKey = imageUrl.replace(ENDPOINT+ "/", "");
        System.out.println("objectKey: " + objectKey);

        // 下载请求
        GetObjectRequest getObjectRequest = new GetObjectRequest(BUCKET_NAME, objectKey);

        try {
            GetObjectResult getObjectResult = ossClient.getObject(getObjectRequest);

            // 读取Object内容
            InputStream inputStream = getObjectResult.getObjectContent();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int len;

            while ((len = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byteArrayOutputStream.close();
            inputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (ClientException e) {
            // 客户端异常，例如网络异常等
            e.printStackTrace();
        } catch (ServiceException e) {
            // 服务端异常
            Log.e("RequestId", e.getRequestId());
            Log.e("ErrorCode", e.getErrorCode());
            Log.e("HostId", e.getHostId());
            Log.e("RawMessage", e.getRawMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean deleteImage(String imageUrl) {

        // 生成object key
        //String objectKey = imageUrl.replace(ENDPOINT+ "/", "");
        //System.out.println("objectKey: " + objectKey);
        String objectKey = ROOT + imageUrl;

        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(BUCKET_NAME, objectKey);

        try {
            ossClient.deleteObject(deleteObjectRequest);
            return true;
        } catch (ClientException e) {
            // 客户端异常，例如网络异常等
            e.printStackTrace();
            return false;
        } catch (ServiceException e) {
            // 服务端异常
            Log.e("RequestId", e.getRequestId());
            Log.e("ErrorCode", e.getErrorCode());
            Log.e("HostId", e.getHostId());
            Log.e("RawMessage", e.getRawMessage());
            return false;
        }
    }
}

