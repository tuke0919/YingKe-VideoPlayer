package com.yingke.videoplayer.tinker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.tencent.tinker.lib.library.TinkerLoadLibrary;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;
import com.yingke.videoplayer.R;
import com.yingke.videoplayer.base.BaseActivity;
import com.yingke.videoplayer.util.DeviceUtil;

/**
 * Tinker热修复测试
 */
public class TinkerDebugActivity extends BaseActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, TinkerDebugActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tinker_debug);

        TextView abi = findViewById(R.id.tv_my_abi);
        abi.setText(abi.getText() + android.os.Build.CPU_ABI);
    }

    @Override
    public boolean hasToolbar() {
        return true;
    }

    @Override
    protected boolean isTransStatusBar() {
        return true;
    }

    /**
     * 加载patch
     * @param view
     */
    public void installPatch(View view) {
        TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk");
    }

    /**
     * 卸载 patch
     * @param view
     */
    public void uninstallPatch(View view) {
        ShareTinkerInternals.killAllOtherProcess(getApplicationContext());
        Tinker.with(getApplicationContext()).cleanPatch();
    }

    /**
     * 显示信息
     * @param view
     */
    public void showInfo(View view) {
        // add more Build Info
        final StringBuilder sb = new StringBuilder();
        Tinker tinker = Tinker.with(getApplicationContext());
        // patch 是否加载
        if (tinker.isTinkerLoaded()) {
            sb.append(String.format("[patch is loaded successfully!] \n"));

            sb.append(String.format("[PackageConfig TINKER_ID] %s \n", tinker.getTinkerLoadResultIfPresent().getPackageConfigByName(ShareConstants.TINKER_ID)));
            sb.append(String.format("[PackageConfig patchMessage] %s \n", tinker.getTinkerLoadResultIfPresent().getPackageConfigByName("patchMessage")));
            sb.append(String.format("[Tinker Patch's Rom Space] %d k \n", tinker.getTinkerRomSpace()));

        } else {
            sb.append(String.format("[patch is not loaded] \n"));
            sb.append(String.format("[Manifest TINKER_ID] %s \n", ShareTinkerInternals.getManifestTinkerID(getApplicationContext())));
        }

        final TextView text = new TextView(this);
        text.setText(sb);
        text.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        text.setTextColor(0xFF000000);
        text.setTypeface(Typeface.MONOSPACE);
        final int padding = 16;
        text.setPadding(padding, padding, padding, padding);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        final AlertDialog alert = builder.create();

        if (!alert.isShowing()) {
            try {
                alert.show();
            } catch (WindowManager.BadTokenException e) {
            }
        }

        WindowManager.LayoutParams params = alert.getWindow().getAttributes();
        params.width = (int) (DeviceUtil.getDeviceWidth(this) * 0.8);
        alert.getWindow().setAttributes(params);
        Window window = alert.getWindow();
        window.setBackgroundDrawableResource(R.color.trans);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setBackgroundColor(getResources().getColor(R.color.white));
        linearLayout.addView(text, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        window.setContentView(linearLayout);
    }

    /**
     * 结束进程
     * @param view
     */
    public void killMySelf(View view) {
        ShareTinkerInternals.killAllOtherProcess(getApplicationContext());
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    /**
     * 使用Hack的方式（测试成功）
     * @param view
     */
    public void loadLibraryHack(View view) {
        String CPU_ABI = android.os.Build.CPU_ABI;
        // 将tinker library中的 CPU_ABI架构的so 注册到系统的library path中。
        TinkerLoadLibrary.installNavitveLibraryABI(this, CPU_ABI);
    }


    /**
     * 不使用Hack的方式（测试失败）
     * @param view
     */
    public void loadLibraryNoHack(View view) {
        String CPU_ABI = android.os.Build.CPU_ABI;
//        TinkerLoadLibrary.loadLibraryFromTinker(getApplicationContext(), "lib/" + CPU_ABI, JniUtil.LIB_NAME);
    }


    /**
     * load lib/armeabi library 未测试
     * @param view
     */
    public void loadLibraryArmeabi(View view) {
//        TinkerLoadLibrary.loadArmLibrary(getApplicationContext(), JniUtil.LIB_NAME);
    }

    /**
     *  load lib/armeabi-v7a library 未测试
     * @param view
     */
    public void loadLibraryArmeabi_V7a(View view) {
//        TinkerLoadLibrary.loadArmV7Library(getApplicationContext(), JniUtil.LIB_NAME);
    }


    /**
     * 测试 修改代码
     * @param view
     */
    public void testFixedJavaCode(View view) {
        Toast.makeText(getApplicationContext(), R.string.debug_tinker_string, Toast.LENGTH_SHORT).show();

    }

    /**
     * 测试 修改so，可编写简单so，测试返回字符串 -未测试
     * @param view
     */
    public void testFixedLibSo(View view) {
//        String string = JniUtil.hello();
//        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
    }

    /**
     * 测试 修改资源 -未测试
     * @param view
     */
    public void testFixedRecourse(View view) {
        ImageView imageView = findViewById(R.id.iv_test_res_image);
        imageView.setImageResource(R.drawable.debug_tinker_image);
//        imageView.setImageResource(R.drawable.ic_launcher);
    }

}
