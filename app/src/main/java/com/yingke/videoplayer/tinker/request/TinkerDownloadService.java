package com.yingke.videoplayer.tinker.request;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.yingke.player.java.util.PlayerLog;
import com.yingke.videoplayer.tinker.utils.TinkerUtils;
import com.yingke.videoplayer.util.AppUtil;
import com.yingke.videoplayer.util.PlayerUtil;
import com.yingke.videoplayer.util.download.AndroidHttpClient;
import com.yingke.videoplayer.util.download.DownloadUtils;
import com.yingke.videoplayer.util.download.HttpUtils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 功能：tinker 热修复 下载tinker patch包
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/17
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class TinkerDownloadService extends IntentService {
    private static final String TAG = "TinkerDownloadService";

    public static final String PATCH_NAME = "patch_signed_7zip.apk";
    public static final String PATCH_TMP = PATCH_NAME + ".temp";
    public static final String PATCH_DIRECTORY = "tinker_patch";

    public static final int DOWNLOAD_SUC = 1000 ;
    public static final int DOWNLOAD_ERR = -1000 ;

    public Handler mHandler;

    public TinkerDownloadService(){
        this(TAG);
    }

    public TinkerDownloadService(String name) {
        super(name);
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == DOWNLOAD_SUC) {
                    // 安装tinker patch
                    File patchFile = new File(getDownloadedPatchFilePath(getApplicationContext()));
                    PlayerLog.d(TAG, "patch file path = " + patchFile.getAbsolutePath());
                    if (patchFile.exists()) {
                        PlayerLog.d(TAG, "patch file is exists, and composing... (cosume time process) ");
                        TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), patchFile.getAbsolutePath());
                    } else {
                        PlayerLog.d(TAG, "patch file is not exists");
                    }
                } else {
                    mHandler.removeMessages(DOWNLOAD_SUC);
                    // TODO 删除下载文件夹
                    TinkerUtils.deleteDownloadedPatchDirectory();

                }
            }
        };
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Uri uri = intent.getData();
        if (uri == null) {
            PlayerLog.e(TAG, "patch url is empty");
            return;
        }
        String url = uri.toString();
        if (TextUtils.isEmpty(url)) {
            PlayerLog.e(TAG, "patch url is empty");
            return;
        }

        AndroidHttpClient httpClient = HttpUtils.getAndroidHttpClient(this);
        HttpResponse response = DownloadUtils.httpDownload(httpClient, url, null, null );
        if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            try {
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    PlayerLog.d(TAG, "patch download net error");
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(DOWNLOAD_ERR);
                    }
                    return;
                }
                Header header = response.getFirstHeader("Content-Disposition");
                String patchDirectoryPath =  getDownloadedPatchDirectory(getApplicationContext());
                File patchDirectory = new File(patchDirectoryPath);
                if (!patchDirectory.exists()) {
                    patchDirectory.mkdirs();
                }
                PlayerLog.d(TAG, "patchDirectoryPath is created " );

                String patchTmpPath = patchDirectory.getAbsolutePath() + "/" + PATCH_TMP;
                File pathTmp = new File(patchTmpPath);
                if (pathTmp.exists()) {
                    pathTmp.delete();
                } else {
                    pathTmp.createNewFile();
                }
                PlayerLog.d(TAG, "patchTmpPath is created : " + patchTmpPath );

                InputStream inStream = entity.getContent();
                FileOutputStream foutStream = new FileOutputStream(pathTmp);
                byte[] buffer = new byte[4 * 1024];
                // 开始下载工作
                while (true) {
                    // 下载过程被打断
                    if (Thread.interrupted()) {
                        PlayerLog.d(TAG, "download patch is interrupted");
                        PlayerLog.d(TAG, "download url : " + url);
                        PlayerLog.d(TAG, "download versionName : " + AppUtil.getNumberVersion(getApplicationContext()));
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(DOWNLOAD_ERR);
                        }
                        break;
                    }
                    int offset = inStream.read(buffer);
                    if (offset == -1) {
                        // 下载已经完成
                        foutStream.close();
                        // rename
                        File destFile = new File(getDownloadedPatchFilePath(getApplicationContext()));
                        if (destFile.exists()) {
                            destFile.delete();
                        }
                        pathTmp.renameTo(destFile);
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(DOWNLOAD_SUC);
                        }
                        PlayerLog.e(TAG, "patch is downloaded successfully ！patchPath = " + destFile.getAbsolutePath() );
                        break;
                    } else {
                        foutStream.write(buffer, 0, offset);
                    }
                }
            } catch (Exception e) {
                PlayerLog.d(TAG, "download url : " + url);
                PlayerLog.d(TAG, "download versionName : " + AppUtil.getNumberVersion(getApplicationContext()));
                PlayerLog.e(TAG, "patch download exception : " + e.getMessage() );
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(DOWNLOAD_ERR);
                }
                e.printStackTrace();
            } finally {
                if (httpClient != null) {
                    httpClient.close();
                    httpClient = null;
                }
            }
        }
    }

    /**
     * @return 下载的patch包文件夹
     */
    public static String getDownloadedPatchDirectory(Context context){
        return context.getFilesDir().getAbsolutePath() + "/" + PATCH_DIRECTORY;
    }

    /**
     * @param context
     * @return 下载的patch包的文件路径
     */
    public static String getDownloadedPatchFilePath(Context context){
        return getDownloadedPatchDirectory(context) + "/" + PATCH_NAME;
    }

}
