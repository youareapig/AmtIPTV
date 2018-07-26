package com.amt.utils.NetUtils.download;



import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Override responsebody to support onprogress callback
 * Created by Kelvin young on 2017/6/12.
 */

public class ResponseProgressBody extends ResponseBody {
    private ResponseBody mResponseBody;
    private DownloadResponseCallback mDownloadResponseCallback;
    private BufferedSource bufferedSource;

    public ResponseProgressBody(ResponseBody responseBody, DownloadResponseCallback downloadResponseCallback){
        this.mResponseBody = responseBody;
        this.mDownloadResponseCallback = downloadResponseCallback;
    }


    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if(bufferedSource == null){
            bufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source){
        return new ForwardingSource(source) {
            long totalBytesRead;
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += ((bytesRead != -1) ? bytesRead : 0);
                if(mDownloadResponseCallback != null){
                    mDownloadResponseCallback.onProgress(totalBytesRead,mResponseBody.contentLength());
                }
                return bytesRead;
            }
        };
    }
}
