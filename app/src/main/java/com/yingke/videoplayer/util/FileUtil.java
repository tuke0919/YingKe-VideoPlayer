package com.yingke.videoplayer.util;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.yingke.videoplayer.util.EncryptUtils.PORT_REC_VIDEO;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-14
 * @email 13661091407@163.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class FileUtil {

    public static final String SUFFIX = ".jpeg";
    public static final String VIDEO_THUMB_DIRECTORY = "list_video_thumb";


    public static File getVideoThumbFile(Context context, String fileName){
        return new File(context.getCacheDir() + "/" + VIDEO_THUMB_DIRECTORY + "/" + fileName + SUFFIX);
    }

    public static File getVideoThumbFile(Context context, String videoUrl, String prefix){
        return getVideoThumbFile(context, EncryptUtils.md5String(videoUrl, prefix));
    }



    /**
     * 将bitmap保存到文件
     * @param bitmap
     * @param path
     */
    public static void saveBitmapToFile(Bitmap bitmap, String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        FileOutputStream fout = null;
        try {
            file.createNewFile();
            fout = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
            fout.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fout != null) {
                    fout.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
