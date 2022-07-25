package com.example.myguide.Utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.view.ContextThemeWrapper;

import com.example.myguide.R;
import com.example.myguide.ui.StudentSetupActivity;
import com.google.android.material.snackbar.Snackbar;

public class SnackBarUtil {

    View viewSnack;
    ContextThemeWrapper ctw;
    Snackbar snack;

    public SnackBarUtil(Context c, View v) {
        ctw = new ContextThemeWrapper(c, R.style.CustomSnackbarTheme);
        snack = Snackbar.make(ctw, v, "Zipcode is incorrect", Snackbar.LENGTH_LONG);
        viewSnack = snack.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)viewSnack.getLayoutParams();
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params.setMargins(0, 40, 0, 0);
        viewSnack.setLayoutParams(params);
    }

    public void setSnackBar(String s) {
        snack.setText(s);
        snack.show();
    }
}
