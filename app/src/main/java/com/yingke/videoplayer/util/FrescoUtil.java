package com.yingke.videoplayer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;

import java.io.File;

/**
 * Fresco 工具
 */
public class FrescoUtil {
    public static final String TAG = "FrescoUtil";


    public static void displayImage(SimpleDraweeView draweeView, File file){
        displayImage(draweeView, Uri.fromFile(file).toString());
    }

    public static void displayImage(SimpleDraweeView draweeView, String uriString){
        draweeView.setImageURI(uriString);
    }


    /**
     * 获取bitmap
     *
     * @param context
     * @param url
     * @param callback
     */
    public static void getBitmap(Context context, String url, final OnGetBitmapCallback callback) {
        if (url == null) {
            if (callback != null)
                callback.onFailure();
            return;
        }

        // #########################
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(url))
                .setRequestPriority(Priority.HIGH)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .build();
        DataSource<CloseableReference<CloseableImage>> dataSource =
                imagePipeline.fetchDecodedImage(imageRequest, context);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            public void onNewResultImpl(Bitmap bitmap) {
                Log.v(TAG, "onNewResultImpl");
                if (callback == null) {
                    return;
                }

                if (bitmap == null) {
                    Log.d(TAG, "Bitmap data source returned success, but bitmap null.");
                    callback.onFailure();
                    return;
                }
                callback.onSuccess(bitmap);
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                Log.v(TAG, "onFailureImpl");
                if (callback == null) {
                    return;
                }
                callback.onFailure();
            }
        }, CallerThreadExecutor.getInstance());
    }

    /**
     * 获取bitmap
     *
     * @param context
     * @param url
     * @param callback
     */
    public static void getBitmap(Context context,
                                 String url,
                                 ResizeOptions options,
                                 final OnGetBitmapCallback callback) {
        if (url == null) {
            if (callback != null)
                callback.onFailure();
            return;
        }
        // #########################
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(url))
                .setRequestPriority(Priority.HIGH)
                .setResizeOptions(options)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .build();
        DataSource<CloseableReference<CloseableImage>> dataSource =
                imagePipeline.fetchDecodedImage(imageRequest, context);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            public void onNewResultImpl(Bitmap bitmap) {
                Log.v(TAG, "onNewResultImpl");
                if (callback == null) {
                    return;
                }

                if (bitmap == null) {
                    Log.d(TAG, "Bitmap data source returned success, but bitmap null.");
                    callback.onFailure();
                    return;
                }
                callback.onSuccess(bitmap);
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                Log.v(TAG, "onFailureImpl");
                if (callback == null) {
                    return;
                }
                callback.onFailure();
            }
        }, CallerThreadExecutor.getInstance());
    }

    public interface OnGetBitmapCallback {
        void onSuccess(Bitmap bitmap);

        void onFailure();
    }


    /**
     * @param path
     * @param draweeView
     * @param resizeOptions
     */
    public static void displayImage(String path, SimpleDraweeView draweeView, ResizeOptions resizeOptions) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(path))
                .setResizeOptions(resizeOptions)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .build();
        draweeView.setController(controller);
    }

    /**
     * @param path
     * @param draweeView
     * @param resizeOptions
     * @param listener
     */
    public static void displayImage(String path, SimpleDraweeView draweeView, ResizeOptions resizeOptions, ControllerListener listener) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(path))
                .setResizeOptions(resizeOptions)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setControllerListener(listener)
                .setAutoPlayAnimations(true)
                .build();
        draweeView.setController(controller);
    }

    /**
     * @param path
     * @param draweeView
     * @param resizeOptions
     * @param processor
     */
    public static void displayImage(String path, SimpleDraweeView draweeView, ResizeOptions resizeOptions, Postprocessor processor) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(path))
                .setPostprocessor(processor)
                .setResizeOptions(resizeOptions)
                .build();
        PipelineDraweeController controller =
                (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setOldController(draweeView.getController())
                        .build();
        draweeView.setController(controller);
    }


}
