package com.yingke.videoplayer.util.download;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-11-17
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class HttpUtils {

    private static final String TAG = "HttpUtils";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final int TIMEOUT = 3000;
    public static final String USER_AGENT = "NTES Android";
    public static final String WAP_PROXY_URL = "10.0.0.172";
    public static final int WAP_PROXY_PORT = 80;
    public static final String DEFAULT_SCHEME_NAME = "http";
    public static final String NETWORK_TYPE_CMWAP = "cmwap";


    /**
     * 同步 请求 外部
     *
     * @param httpClient
     * @param url
     * @param params
     * @param headers
     * @param method
     * @param encoding
     * @return
     */
    public static HttpResponse doHttpExecute(HttpClient httpClient, String url, List<NameValuePair> params, Header[] headers, String method, String encoding) {
        HttpUriRequest request = null;
        if ("POST".equals(method)) {
            HttpPost post = new HttpPost(url);
            if (params != null) {
                try {
                    post.setEntity(new UrlEncodedFormEntity(params, TextUtils.isEmpty(encoding) ? "UTF-8" : encoding));
                } catch (UnsupportedEncodingException var9) {
                    var9.printStackTrace();
                }
            }

            request = post;
        } else if ("GET".equals(method)) {
            if (params != null && params.size() > 0) {
                url = url + "?" + URLEncodedUtils.format(params, TextUtils.isEmpty(encoding) ? "UTF-8" : encoding);
            }

            request = new HttpGet(url);
        }

        HttpResponse response = null;
        if (request != null) {
            response = doHttpExecute(httpClient, (HttpUriRequest)request, headers);
        }

        return response;
    }

    /**
     * 内部 同步请求
     *
     * @param httpClient
     * @param request
     * @param headers
     * @return
     */
    private static HttpResponse doHttpExecute(HttpClient httpClient, HttpUriRequest request, Header[] headers) {
        try {
            if (headers != null) {
                Header[] var3 = headers;
                int var4 = headers.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    Header header = var3[var5];
                    request.addHeader(header);
                }
            }

            return httpClient.execute(request);
        } catch (ClientProtocolException var7) {
            var7.printStackTrace();
        } catch (IOException var8) {
            var8.printStackTrace();
        }

        return null;
    }

    /**
     * 获取http客户端
     *
     * @param context
     * @return
     */
    public static AndroidHttpClient getAndroidHttpClient(Context context) {
        AndroidHttpClient httpclient = AndroidHttpClient.newInstance("NTES Android", context);
        initHttpClient(context, httpclient);
        return httpclient;
    }

    /**
     * @param context
     * @param httpClient
     */
    private static void initHttpClient(Context context, HttpClient httpClient) {
        if (isCMWAP(context)) {
            HttpHost proxy = new HttpHost("10.0.0.172", 80, "http");
            httpClient.getParams().setParameter("http.route.default-proxy", proxy);
        }

        HttpClientParams.setRedirecting(httpClient.getParams(), true);
    }

    /**
     * @param context
     * @return
     */
    public static boolean isCMWAP(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getType() == 0 && "cmwap".equals(networkInfo.getExtraInfo())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 构建 通用参数
     *
     * @param paramMap
     * @param encodeCharset
     * @return
     */
    public static StringBuffer buildCommonParams(Map<String, String> paramMap, String encodeCharset) {
        StringBuffer paramBuffer = new StringBuffer();
        Set keySet = paramMap.keySet();
        Iterator iterator = keySet.iterator();

        String key;
        try {
            while(iterator.hasNext()) {
                String key1 = (String)iterator.next();
                key = (String)paramMap.get(key1);
                paramBuffer.append("&").append(URLEncoder.encode(key, encodeCharset)).append("=").append(URLEncoder.encode(key, encodeCharset));
            }
        } catch (UnsupportedEncodingException var8) {
            while(iterator.hasNext()) {
                key = (String)iterator.next();
                String value = (String)paramMap.get(key);
                paramBuffer.append("&").append(key).append("=").append(value);
            }
        }

        if (paramBuffer.length() > 0) {
            paramBuffer.deleteCharAt(0);
        }

        return paramBuffer;
    }

    /**
     * @param input1
     * @return
     * @throws Exception
     */
    public static String getGZipResult(InputStream input1) throws Exception {
        BufferedInputStream bis = new BufferedInputStream(input1);
        bis.mark(2);
        byte[] header = new byte[2];
        int ret = bis.read(header);
        bis.reset();
        int ss = header[0] & 255 | (header[1] & 255) << 8;
        Object input;
        if (ret != -1 && ss == 35615) {
            input = new GZIPInputStream(bis);
        } else {
            input = bis;
        }

        StringBuilder sb = new StringBuilder();

        int s;
        while((s = ((InputStream)input).read()) != -1) {
            sb.append((char)s);
        }

        bis.close();
        return sb.toString();
    }


}
