package com.yingke.videoplayer.home.landscape.dialog;

import android.app.Dialog;
import android.os.Bundle;

import com.yingke.videoplayer.home.landscape.dialog.HalfTransDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-31
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
@Deprecated
public class HalfTransDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new HalfTransDialog(getContext());
    }
}
