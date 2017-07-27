package com.fuexpress.kr.ui.view;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;

/**
 * Created by Longer on 2016/10/26.
 */
public class CustomToast extends Toast {
    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */

    private static TextView messageTv;
    private View rootView;


    public CustomToast(Context context) {
        super(context);
        rootView = LayoutInflater.from(context).inflate(R.layout.toast, null);
        messageTv = (TextView) rootView.findViewById(R.id.toast_message_tv);
        setView(rootView);
    }

    public static void setMessage(CharSequence message){
        messageTv.setText(message);
    }
    public @interface Duration {}
    public static CustomToast makeText(Context context, CharSequence text, @Duration int duration) {
        CustomToast result = new CustomToast(context);
        setMessage(text);
        result.setDuration(duration);

        return result;
    }
}
