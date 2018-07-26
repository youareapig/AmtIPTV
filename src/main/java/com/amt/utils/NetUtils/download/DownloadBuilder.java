package com.amt.utils.NetUtils.download;




import com.amt.utils.NetUtils.MyOkHttp;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Download Builder
 * Created by Kelvin young on 2017/6/9.
 */

public class DownloadBuilder {
    private MyOkHttp mMyOkHttp;
    private String mUrl = "";
    private Map<String,String> mHeaders;
    private String mFileDir = "";     //file direction
    private String mFileName = "";    //file name
    private String mFilePath = "";    // No need to set previous two attributes if you set this attribute(eg:/cache/recovery/update.zip)
    private Long mCompleteBytes = 0L; // bytes already download use for breakpoint downloading

    public DownloadBuilder(MyOkHttp myOkHttp){
        this.mMyOkHttp = myOkHttp;
    }

    /**
     * Set download url
     * @param url
     * @return
     */
    public DownloadBuilder url(String url){
        this.mUrl = url;
        return this;
    }

    /**
     * Set download file direction
     * @param fileDir
     * @return
     */
    public DownloadBuilder fileDir(String fileDir){
        this.mFileDir = fileDir;
        return this;
    }

    /**
     * Set download file storage name
     * @param fileName
     * @return
     */
    public DownloadBuilder fileName(String fileName){
        this.mFileName = fileName;
        return this;
    }

    /**
     * Set download file storage path
     * @param filePath
     * @return
     */
    public DownloadBuilder filePath(String filePath){
        this.mFilePath = filePath;
        return this;
    }

    /**
     * Set download network request headers
     * @param headers
     * @return
     */
    public DownloadBuilder headers(Map<String,String> headers){
        this.mHeaders = headers;
        return this;
    }

    /**
     * Add download network request header one by one
     * @param key
     * @param value
     * @return
     */
    public DownloadBuilder addHeader(String key, String value){
        if(this.mHeaders == null){
            mHeaders = new LinkedHashMap<String,String>();
        }
        mHeaders.put(key,value);
        return this;
    }

    /**
     * Set already download bytes(use for breakpoint download)
     * Set 0L if you want to start a new full range download
     * @param completeBytes
     * @return
     */
    public DownloadBuilder setCompleteBytes(Long completeBytes){
        if(completeBytes > 0L) {
            this.mCompleteBytes = completeBytes;
            addHeader("RANGE", "bytes=" + completeBytes + "-");     //add breakpoint download header
        }
        return this;
    }

    public Call enqueue(final DownloadResponseCallback downloadResponseCallback){
        try {
            if (mUrl.length() == 0) {
                throw new IllegalArgumentException("Url can not be null !");
            }
            if(mFilePath.length() == 0) {
                if(mFileDir.length() == 0 || mFileName.length() == 0) {
                    throw new IllegalArgumentException("FilePath can not be null !");
                } else {
                    mFilePath = mFileDir + mFileName;
                }
            }
            checkfilepath(mFilePath, mCompleteBytes);
            Request.Builder builder = new Request.Builder().url(mUrl);

            appendHeaders(builder,mHeaders);

            Request request = builder.build();

            Call call = mMyOkHttp.getOkHttpClient().newBuilder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Response originalresponse = chain.proceed(chain.request());
                            return originalresponse.newBuilder()
                                    .body(new ResponseProgressBody(originalresponse.body(), downloadResponseCallback))
                                    .build();
                        }
                    })
                    .build()
                    .newCall(request);
            call.enqueue(new MyDownloadCallback(downloadResponseCallback, mFilePath, mCompleteBytes));
            return call;
        }catch (Exception e){
            e.printStackTrace();
            downloadResponseCallback.onFail(e.getMessage());
            return null;
        }
    }

    /**
     * Check file path availability
     * @param filePath
     * @param completeBytes   break point bytes
     * @throws Exception
     */
    public void checkfilepath(String filePath, Long completeBytes) throws Exception {
        File file = new File(filePath);
        if(file.exists()){
            return;
        }

        if(completeBytes > 0L){     //if set break point download , file must be existed!!
            throw new Exception("Break point file " + filePath + " does not exist");
        }

        if(filePath.endsWith(File.separator)){
            throw new Exception("Create file " + filePath + " fail, target can not be directory");
        }

        //Check target file parent directory existence
        if(!file.getParentFile().exists()){
            if(file.getParentFile().mkdirs()){
                throw new Exception("Create target file parent directory fail");
            }
        }
    }

    /**
     * Add request headers
     * @param builder
     * @param headers
     */
    private void appendHeaders(Request.Builder builder, Map<String, String> headers){
        Headers.Builder headerBuilder = new Headers.Builder();
        if(headers == null || headers.isEmpty()){
            return;
        }
        for(String key : headers.keySet()){
            headerBuilder.add(key,headers.get(key));
        }
        builder.headers(headerBuilder.build());
    }
}
