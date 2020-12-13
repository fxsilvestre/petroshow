package com.viasoft.petroshow.ui.util;

import android.content.Context;
import android.widget.Toast;

public class MensagemToastShort {

    public static void mostrarMsg(Context c, String msg) {
        Toast.makeText(
                c,
                msg,
                Toast.LENGTH_SHORT
        ).show();
    }
}
