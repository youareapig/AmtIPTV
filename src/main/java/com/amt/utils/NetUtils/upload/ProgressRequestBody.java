package com.amt.utils.NetUtils.upload;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Override request body set upload progress listener
 * Created by Kelvin young on 2017/7/17.
 */
public class ProgressRequestBody extends RequestBody {

    private IResponseHandler mResponseHandler;      //Callback listener
    private RequestBody mRequestBody;
    private BufferedSink mBufferedSink;

    public ProgressRequestBody(RequestBody requestBody, IResponseHandler responseHandler) {
        this.mResponseHandler = responseHandler;
        this.mRequestBody = requestBody;
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if(mBufferedSink == null) {
            mBufferedSink = Okio.buffer(sink(sink));
        }

        //write
        mRequestBody.writeTo(mBufferedSink);
        //must flush,otherwise last part of data may no be written
        mBufferedSink.flush();
    }

    /**
     * 写入，回调进度接口
     * @param sink Sink
     * @return Sink
     */
    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            //当前写入字节数
            long bytesWritten = 0L;
            //总字节长度，避免多次调用contentLength()方法
            long contentLength = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    //获得contentLength的值，后续不再调用
                    contentLength = contentLength();
                }
                //增加当前写入的字节数
                bytesWritten += byteCount;
                //回调
                mResponseHandler.onProgress(bytesWritten, contentLength);
            }
        };
    }
}
