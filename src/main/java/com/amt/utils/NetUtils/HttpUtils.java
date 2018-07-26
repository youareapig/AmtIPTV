package com.amt.utils.NetUtils;

import android.text.TextUtils;

import com.amt.utils.ALOG;
import com.amt.utils.NetUtils.download.DownloadResponseCallback;
import com.amt.utils.NetUtils.upload.IResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.amt.utils.mainthread.MainThreadSwitcher.runOnMainThreadSync;

/**
 * Created by Kelvin young on 2017/5/22.
 * Class of network request, file download, file uplaod
 * base on OKHTTP open source code
 */

public class HttpUtils {
    private static OkHttpClient mOkHttpClient = null;
    private static HttpUtils mInstance = null;
    private static MyOkHttp mMyOkHttp = null;
    private static int TIMEOUT = 5*1000;


    public static HttpUtils getInstance(){
        if(mInstance == null){
            synchronized(HttpUtils.class){
                if(mInstance == null){
                    mInstance = new HttpUtils();
                }
            }
        }
        return mInstance;
    }
    /**
     * LYN
     * 解析出url请求的路径，包括页面
     *
     * @param strURL
     *            url地址
     * @return url路径
     */
    public static String UrlPage(String strURL) {
        String strPage = null;
        String[] arrSplit = null;
        strURL = strURL.trim();
        arrSplit = strURL.split("[?]");
        if (strURL.length() > 0) {
            if (arrSplit.length > 1) {
                if (arrSplit[0] != null) {
                    strPage = arrSplit[0];
                }
            }
        }
        return strPage;
    }

    /**
     * LYN
     * 去掉url中的路径，留下请求参数部分
     *
     * @param strURL
     *            url地址
     * @return url请求参数部分
     */
    private static String TruncateUrlPage(String strURL) {
        String strAllParam = null;
        String[] arrSplit = null;
        strURL = strURL.trim();
        arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                if (arrSplit[1] != null) {
                    strAllParam = arrSplit[1];
                }
            }
        }
        return strAllParam;
    }

    /**
     * LYN
     * 解析出url参数中的键值对 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     *
     * @param URL
     *            url地址
     * @return url请求参数部分
     */
    public static Map<String, String> URLRequest(String URL) {
        Map<String, String> mapRequest = new HashMap<String, String>();
        String[] arrSplit = null;
        String strUrlParam = TruncateUrlPage(URL);
        ALOG.debug("strUrlParam:"+strUrlParam);
        if (strUrlParam == null) {
            return mapRequest;
        }
        // 每个键值为一组
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");
            // 解析出键值
            if (arrSplitEqual.length > 1) {
                // 正确解析
                ALOG.debug("key:"+arrSplitEqual[0]+"--value:"+arrSplitEqual[1]);
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            } else {
                if (arrSplitEqual[0] != "") {
                    // 只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;

    }

    /***
     * LYN
     * 获取url ip
     * @param url
     * @return
     */
    public static String getIP(String url) {
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        URI effectiveURI = null;

        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), null, null, null);
        } catch (Throwable var4) {
            effectiveURI = null;
        }
        ALOG.debug("IP:"+effectiveURI);
        return effectiveURI.toString();
    }
    /**
     * LYN
     * 向指定URL发送GET方法的请求
     *
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param,NetCallback callback) {
        String result = "";
        BufferedReader in = null;
        // 获取所有响应头字段
        Map<String, List<String>> map = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                ALOG.debug(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            ALOG.debug("发送GET请求出现异常！" + e);
            if(callback!=null){
                callback.onFail(e.getMessage());
            }
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                if(callback!=null){
                    callback.onFail(e2.getMessage());
                }
                e2.printStackTrace();
            }
        }
        if(!TextUtils.isEmpty(result)){
            if(callback!=null){

                callback.onSuccess(result);
            }
        }
        ALOG.debug("===result:"+result);
        return result;
    }

    /**
     * Use GET method to request
     * @param url
     * @param callback
     */
    public static void get(String url, final NetCallback callback){
        mOkHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final String error = e.toString();
                runOnMainThreadSync(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFail(error);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                runOnMainThreadSync(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(str);
                    }
                });
            }
        });
    }

    /**
     * Get Method request with specific headers
     * @param url
     * @param headers use new Headers.Builder().add(String key,String value).build() to construct
     * @param callback
     */
    public static void get(String url,Headers headers,final NetCallback callback){
        mOkHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final String error = e.toString();
                runOnMainThreadSync(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFail(error);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                runOnMainThreadSync(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(str);
                    }
                });
            }
        });
    }
    /**
     * lyn
     * post请求，并提交json数据
     * @param ADD_URL
     * @param key
     * @param value
     */
    public static void post(String ADD_URL,String key,String value) {
        ALOG.debug("ADD_URL:"+ADD_URL+">>>>key:"+key+">>>>>>value:"+value);
        try {
            //创建连接
            URL url = new URL(ADD_URL);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.connect();

            //POST请求
            DataOutputStream out = new DataOutputStream(
                    connection.getOutputStream());
            JSONObject obj = new JSONObject();
            obj.accumulate(key,value);

            out.writeBytes(obj.toString());
            out.flush();
            out.close();

            //读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String lines;
            StringBuffer sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sb.append(lines);
            }
            ALOG.debug("=====sbbbbb:"+sb);
            reader.close();
            // 断开连接
            connection.disconnect();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    /**
     * Use POST method to request
     * @param url
     * @param data RequestBody class : use function FormBody.Builder.add().build() to generate data
     * @param callback
     */
    public static void post(String url, RequestBody data, final NetCallback callback){
        mOkHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(url)
                .post(data)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final String error = e.toString();
                runOnMainThreadSync(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFail(error);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                runOnMainThreadSync(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(str);
                    }
                });
            }

        });
    }

    /**
     * Combine url and params as a new url
     * @param url
     * @param params
     * @return
     */
    public static String addrequesturlparams(String url, LinkedHashMap<String,String> params){
        Iterator<String> keys = params.keySet().iterator();
        Iterator<String> values = params.values().iterator();
        StringBuffer sb = new StringBuffer();
        sb.append("?");
        for(int i = 0; i < params.size(); i++){
            sb.append(keys.next()+"="+values.next());
            if(i != params.size()-1){
                sb.append("&");
            }
        }
        return url+sb.toString();
    }

    /**
     * Obtain 302 redirect location
     * @param url
     * @param callback
     * @return
     */
    public static void get302Location(String url,final NetCallback callback){
        mOkHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .followRedirects(false)
                .followSslRedirects(false)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final String error = e.toString();
                runOnMainThreadSync(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFail(error);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code() == HttpURLConnection.HTTP_MOVED_TEMP){
                    final String location = response.headers().get("Location");
                    runOnMainThreadSync(new Runnable() {
                        @Override
                        public void run() {
                            callback.on302Moved(location);
                        }
                    });
                }else if(response.code() == HttpURLConnection.HTTP_OK){
                    final String str = response.body().string();
                    runOnMainThreadSync(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(str);
                        }
                    });
                }
            }
        });
    }


    /**
     * Download method base on okhttp
     * @param url
     * @param filepath
     * @param downloadResponseCallback
     */
    public static void download(String url, String filepath, DownloadResponseCallback downloadResponseCallback){
        //custom okhttpclient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();
        mMyOkHttp = new MyOkHttp(okHttpClient);
        Long breakpoint = 0L;
        File f = new File(filepath);
        if(f.length()>0){
            breakpoint = f.length();
        }
        mMyOkHttp.download()
                .url(url)
                .filePath(filepath)
                .setCompleteBytes(breakpoint)
                .enqueue(downloadResponseCallback)
                .request();
    }


    /**
     * Upload method base on okhttp
     * @param url
     * @param filename
     * @param file
     * @param iResponseHandler
     */
    public static void upload(String url, String filename, File file, IResponseHandler iResponseHandler){
        //custom okhttpclient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();
        mMyOkHttp = new MyOkHttp(okHttpClient);
        mMyOkHttp.upload()
                .url(url)
                .addFile(filename,file)
                .enqueue(iResponseHandler);
    }



}
