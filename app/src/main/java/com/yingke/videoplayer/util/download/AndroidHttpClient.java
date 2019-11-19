package com.yingke.videoplayer.util.download;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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
public final class AndroidHttpClient implements HttpClient {

    public static long DEFAULT_SYNC_MIN_GZIP_BYTES = 256L;
    private static final String TAG = "AndroidHttpClient";
    private static final HttpRequestInterceptor sThreadCheckInterceptor = new HttpRequestInterceptor() {
        public void process(HttpRequest request, HttpContext context) {
            if (Looper.myLooper() != null && Looper.myLooper() == Looper.getMainLooper()) {
                throw new RuntimeException("This thread forbids HTTP requests");
            }
        }
    };
    private final HttpClient delegate;
    private RuntimeException mLeakedException = new IllegalStateException("AndroidHttpClient created and never closed");
    private volatile AndroidHttpClient.LoggingConfiguration curlConfiguration;

    public static AndroidHttpClient newInstance(String userAgent, Context context) {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpConnectionParams.setConnectionTimeout(params, 20000);
        HttpConnectionParams.setSoTimeout(params, 60000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        HttpClientParams.setRedirecting(params, false);
        HttpProtocolParams.setUserAgent(params, userAgent);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
        if (Build.VERSION.SDK_INT < 23) {
            socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        }

        schemeRegistry.register(new Scheme("https", socketFactory, 443));
        ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);
        return new AndroidHttpClient(manager, params);
    }

    public static AndroidHttpClient newInstance(String userAgent) {
        return newInstance(userAgent, (Context)null);
    }

    private AndroidHttpClient(ClientConnectionManager ccm, HttpParams params) {
        this.delegate = new DefaultHttpClient(ccm, params) {
            protected BasicHttpProcessor createHttpProcessor() {
                BasicHttpProcessor processor = super.createHttpProcessor();
                processor.addRequestInterceptor(AndroidHttpClient.sThreadCheckInterceptor);
                processor.addRequestInterceptor(AndroidHttpClient.this.new CurlLogger());
                return processor;
            }

            protected HttpContext createHttpContext() {
                HttpContext context = new BasicHttpContext();
                context.setAttribute("http.authscheme-registry", this.getAuthSchemes());
                context.setAttribute("http.cookiespec-registry", this.getCookieSpecs());
                context.setAttribute("http.auth.credentials-provider", this.getCredentialsProvider());
                return context;
            }
        };
    }

    protected void finalize() throws Throwable {
        super.finalize();
        if (this.mLeakedException != null) {
            Log.e("AndroidHttpClient", "Leak found in finalize()", this.mLeakedException);
            this.mLeakedException = null;
        }

    }

    public static void modifyRequestToAcceptGzipResponse(HttpRequest request) {
        request.addHeader("Accept-Encoding", "gzip");
    }

    public static InputStream getUngzippedContent(HttpEntity entity) throws IOException {
        InputStream responseStream = entity.getContent();
        if (responseStream == null) {
            return (InputStream)responseStream;
        } else {
            Header header = entity.getContentEncoding();
            if (header == null) {
                return (InputStream)responseStream;
            } else {
                String contentEncoding = header.getValue();
                if (contentEncoding == null) {
                    return (InputStream)responseStream;
                } else {
                    if (contentEncoding.contains("gzip")) {
                        responseStream = new GZIPInputStream((InputStream)responseStream);
                    }

                    return (InputStream)responseStream;
                }
            }
        }
    }

    public void close() {
        if (this.mLeakedException != null) {
            this.getConnectionManager().shutdown();
            this.mLeakedException = null;
        }

    }

    public HttpParams getParams() {
        return this.delegate.getParams();
    }

    public ClientConnectionManager getConnectionManager() {
        return this.delegate.getConnectionManager();
    }

    public HttpResponse execute(HttpUriRequest request) throws IOException {
        return this.delegate.execute(request);
    }

    public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException {
        return this.delegate.execute(request, context);
    }

    public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException {
        return this.delegate.execute(target, request);
    }

    public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException {
        return this.delegate.execute(target, request, context);
    }

    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        return this.delegate.execute(request, responseHandler);
    }

    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
        return this.delegate.execute(request, responseHandler, context);
    }

    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        return this.delegate.execute(target, request, responseHandler);
    }

    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
        return this.delegate.execute(target, request, responseHandler, context);
    }

    public static AbstractHttpEntity getCompressedEntity(byte[] data, ContentResolver resolver) throws IOException {
        ByteArrayEntity entity;
        if ((long)data.length < getMinGzipSize(resolver)) {
            entity = new ByteArrayEntity(data);
        } else {
            ByteArrayOutputStream arr = new ByteArrayOutputStream();
            OutputStream zipper = new GZIPOutputStream(arr);
            zipper.write(data);
            zipper.close();
            entity = new ByteArrayEntity(arr.toByteArray());
            entity.setContentEncoding("gzip");
        }

        return entity;
    }

    public static long getMinGzipSize(ContentResolver resolver) {
        return DEFAULT_SYNC_MIN_GZIP_BYTES;
    }

    public void enableCurlLogging(String name, int level) {
        if (name == null) {
            throw new NullPointerException("name");
        } else if (level >= 2 && level <= 7) {
            this.curlConfiguration = new AndroidHttpClient.LoggingConfiguration(name, level);
        } else {
            throw new IllegalArgumentException("Level is out of range [2..7]");
        }
    }

    public void disableCurlLogging() {
        this.curlConfiguration = null;
    }

    private static String toCurl(HttpUriRequest request, boolean logAuthToken) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("curl ");
        Header[] var3 = request.getAllHeaders();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Header header = var3[var5];
            if (logAuthToken || !header.getName().equals("Authorization") && !header.getName().equals("Cookie")) {
                builder.append("--header \"");
                builder.append(header.toString().trim());
                builder.append("\" ");
            }
        }

        URI uri = request.getURI();
        if (request instanceof RequestWrapper) {
            HttpRequest original = ((RequestWrapper)request).getOriginal();
            if (original instanceof HttpUriRequest) {
                uri = ((HttpUriRequest)original).getURI();
            }
        }

        builder.append("\"");
        builder.append(uri);
        builder.append("\"");
        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest)request;
            HttpEntity entity = entityRequest.getEntity();
            if (entity != null && entity.isRepeatable()) {
                if (entity.getContentLength() < 1024L) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    entity.writeTo(stream);
                    String entityString = stream.toString();
                    builder.append(" --data-ascii \"").append(entityString).append("\"");
                } else {
                    builder.append(" [TOO MUCH DATA TO INCLUDE]");
                }
            }
        }

        return builder.toString();
    }

    private class CurlLogger implements HttpRequestInterceptor {
        private CurlLogger() {
        }

        public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
            AndroidHttpClient.LoggingConfiguration configuration = AndroidHttpClient.this.curlConfiguration;
            if (configuration != null && configuration.isLoggable() && request instanceof HttpUriRequest) {
                configuration.println(AndroidHttpClient.toCurl((HttpUriRequest)request, false));
            }

        }
    }

    private static class LoggingConfiguration {
        private final String tag;
        private final int level;

        private LoggingConfiguration(String tag, int level) {
            this.tag = tag;
            this.level = level;
        }

        private boolean isLoggable() {
            return Log.isLoggable(this.tag, this.level);
        }

        private void println(String message) {
            Log.println(this.level, this.tag, message);
        }
    }
}
