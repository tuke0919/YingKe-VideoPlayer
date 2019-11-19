package com.yingke.videoplayer.util.download;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicHeader;

import java.util.List;

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
public class DownloadUtils {


    /**
     * 请求http下载 分段下载
     * @param httpClient
     * @param url
     * @param startPos
     * @param endPos
     * @param params
     * @param encoding
     * @return
     */
    public static HttpResponse httpDownload(HttpClient httpClient,
                                            String url,
                                            int startPos,
                                            int endPos,
                                            List<NameValuePair> params,
                                            String encoding) {
        Header[] headers = new Header[7];

        headers[0] = new BasicHeader(
                "Accept",
                "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, "
                        + "application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, "
                        + "application/vnd.ms-powerpoint, application/msword, */*");
        headers[1] = new BasicHeader("Accept-Language", "zh-CN");
        headers[2] = new BasicHeader("Referer", url);
        headers[3] = new BasicHeader("Charset", "UTF-8");
        headers[4] = new BasicHeader("Range", "bytes=" + startPos + "-" + endPos);
        headers[5] = new BasicHeader(
                "User-Agent",
                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; "
                        + ".NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
        headers[6] = new BasicHeader("Connection", "Keep-Alive");

        return HttpUtils.doHttpExecute(httpClient, url, params, headers, HttpUtils.GET, encoding);
    }

    /**
     * 请求http下载,不分段
     * @param httpClient
     * @param url
     * @param params
     * @param encoding
     * @return
     */
    public static HttpResponse httpDownload(HttpClient httpClient,
                                            String url,
                                            List<NameValuePair> params,
                                            String encoding) {
        Header[] headers = new Header[6];

        headers[0] = new BasicHeader(
                "Accept",
                "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, "
                        + "application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, "
                        + "application/vnd.ms-powerpoint, application/msword, */*");
        headers[1] = new BasicHeader("Accept-Language", "zh-CN");
        headers[2] = new BasicHeader("Referer", url);
        headers[3] = new BasicHeader("Charset", "UTF-8");
        headers[4] = new BasicHeader(
                "User-Agent",
                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; "
                        + ".NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
        headers[5] = new BasicHeader("Connection", "Keep-Alive");

        return HttpUtils.doHttpExecute(httpClient, url, params, headers, HttpUtils.GET, encoding);
    }


}
