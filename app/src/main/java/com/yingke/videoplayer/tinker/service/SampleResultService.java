/*
 * Tencent is pleased to support the open source community by making Tinker available.
 *
 * Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yingke.videoplayer.tinker.service;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.tencent.tinker.lib.service.DefaultTinkerResultService;
import com.tencent.tinker.lib.service.PatchResult;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.lib.util.TinkerServiceInternals;
import com.yingke.videoplayer.NetConstants;
import com.yingke.videoplayer.tinker.utils.TinkerUtils;

import java.io.File;


/**
 * AbstractResultService类是:patch补丁合成进程将合成结果返回给主进程的类。
 * 需要在AndroidManifest上添加你的Service。
 * <p>
 * optional, you can just use DefaultTinkerResultService
 * we can restart process when we are at background or screen off
 * Created by zhangshaowen on 16/4/13.
 */
public class SampleResultService extends DefaultTinkerResultService {
    private static final String TAG = "Tinker.SampleResultService";

    @Override
    public void onPatchResult(final PatchResult result) {
        if (result == null) {
            TinkerLog.e(TAG, "SampleResultService received null result!!!!");
            return;
        }
        TinkerLog.i(TAG, "SampleResultService receive result: %s", result.toString());

        //first, we want to kill the recover process
        TinkerServiceInternals.killTinkerPatchServiceProcess(getApplicationContext());
        // 加载成功后显示的toast，可以不要
        if (NetConstants.TEST) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (result.isSuccess) {
                        Toast.makeText(getApplicationContext(), "patch success, please restart process", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "patch fail, please check reason", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        // 安装patch成功后的操作
        if (result.isSuccess) {
            // 安装成功，删除文件patch文件
            deleteRawPatchFile(new File(result.rawPatchFilePath));

            // 删除下载的Patch包文件夹
            TinkerUtils.deleteDownloadedPatchDirectory();
            // 保存合成成功的PatchMd5
            TinkerUtils.saveComposedOrInstalledPatchMd5(result.patchVersion);


            //not like TinkerResultService, I want to restart just when I am at background!
            //if you have not install tinker this moment, you can use TinkerApplicationHelper api
            // 检查是否需要 重启
            if (checkIfNeedKill(result)) {
                if (TinkerUtils.isBackground()) {
                    // 后台重启
                    TinkerLog.i(TAG, "it is in background, just restart process");
                    restartProcess();
                } else {
                    //we can wait process at background, such as onAppBackground
                    //or we can restart when the screen off
                    // 锁屏重启
                    TinkerLog.i(TAG, "tinker wait screen to restart process");
                    new TinkerUtils.ScreenState(getApplicationContext(), new TinkerUtils.ScreenState.IOnScreenOff() {
                        @Override
                        public void onScreenOff() {
                            restartProcess();
                        }
                    });
                }
            } else {
                TinkerLog.i(TAG, "I have already install the newly patch version!");
            }
        } else {
            // 安装patch错误
            TinkerLog.i(TAG, "tinker installed error");
            // 删除下载的Patch包文件夹
            TinkerUtils.deleteDownloadedPatchDirectory();
        }
    }

    /**
     * you can restart your process through service or broadcast
     */
    private void restartProcess() {
        TinkerLog.i(TAG, "app is background now, i can kill quietly");
        //you can send service or broadcast intent to restart your process
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
