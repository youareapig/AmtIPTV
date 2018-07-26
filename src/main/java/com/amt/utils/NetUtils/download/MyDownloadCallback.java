package com.amt.utils.NetUtils.download;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Call back implementation
 * Created by Kelvin young on 2017/6/12.
 */

public class MyDownloadCallback implements Callback {
    private DownloadResponseCallback mDownloadReponseCallback;
    private String mFilePath;
    private Long mCompleteBytes;
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    public MyDownloadCallback(DownloadResponseCallback downloadResponseCallback, String filePath, Long completeBytes){
        this.mDownloadReponseCallback = downloadResponseCallback;
        this.mFilePath = filePath;
        this.mCompleteBytes = completeBytes;
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mDownloadReponseCallback != null){
                    mDownloadReponseCallback.onFail(e.toString());
                }
            }
        });

    }

    @Override
    public void onResponse(Call call, final Response response) throws IOException {
        ResponseBody body = response.body();
        try{
            if(response.isSuccessful()){
                //onStart
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mDownloadReponseCallback != null){
                            mDownloadReponseCallback.onStart(response.body().contentLength());
                        }
                    }
                });

                try {
                    //Do not support break point download and need to redownload if no content-range return
                    if (response.header("Content-Range") == null || response.header("Content-Range").length() == 0) {
                        mCompleteBytes = 0L;
                    }

                    saveFile(response, mFilePath, mCompleteBytes);

                    final File file = new File(mFilePath);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mDownloadReponseCallback != null) {
                                mDownloadReponseCallback.onFinish(file);
                            }
                        }
                    });
                }catch (final Exception e){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mDownloadReponseCallback != null){
                                mDownloadReponseCallback.onFail(e.toString());
                            }
                        }
                    });
                }
            }else{
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mDownloadReponseCallback != null){
                            mDownloadReponseCallback.onFail("Fail status : " + response.code());
                        }
                    }
                });
            }
        }finally {
            if(body != null){
                body.close();
            }
        }
    }

    private void saveFile(Response response, String filePath, Long completeBytes) throws Exception {
        InputStream is = null;
        byte[] buffer = new byte[1*1024];
        int len;
        RandomAccessFile file = null;
        try{
            is = response.body().byteStream();
            file = new RandomAccessFile(filePath,"rwd");
            if(completeBytes > 0L){
                file.seek(completeBytes);
            }

            long complete_len = 0;
            final long total_len = response.body().contentLength();
            while ((len = is.read(buffer)) != -1){
                file.write(buffer,0,len);
                complete_len += len;
                final long final_complete_len = complete_len;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mDownloadReponseCallback != null){
                            mDownloadReponseCallback.onProgress(final_complete_len,total_len);
                        }
                    }
                });
            }
        }finally {
            try{
                if(is != null){
                    is.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            try{
                if(file != null){
                    file.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
