package com.fuexpress.kr.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.ui.uiutils.UIUtils;

/**
 * Created by Longer on 2016/11/2.
 */
public class CustomDialog extends Dialog {
    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private boolean vertical;
        private Context context; // 上下文对象
        private String title; // 对话框标题
        private String message; // 对话框内容
        private String confirm_btnText; // 按钮名称“确定”
        private String cancel_btnText; // 按钮名称“取消”
        private String neutral_btnText; // 按钮名称“隐藏”
        private View contentView; // 对话框中间加载的其他布局界面
        /* 按钮坚挺事件 */
        private OnClickListener confirm_btnClickListener;
        private OnClickListener cancel_btnClickListener;
        private OnClickListener neutral_btnClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        /* 设置对话框信息 */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog button orientation
         *
         * @param vertical
         * @return Builder
         */
        public Builder setVertical(boolean vertical) {
            this.vertical = vertical;
            return this;
        }


        /**
         * Set the Dialog message from resource
         *
         * @param message
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * 设置对话框界面
         *
         * @param v View
         * @return
         */
        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param confirm_btnText
         * @return
         */
        public Builder setPositiveButton(int confirm_btnText,
                                         OnClickListener listener) {
            this.confirm_btnText = (String) context.getText(confirm_btnText);
            this.confirm_btnClickListener = listener;
            return this;
        }

        /**
         * Set the positive button and it's listener
         *
         * @param confirm_btnText
         * @return
         */
        public Builder setPositiveButton(String confirm_btnText,
                                         OnClickListener listener) {
            this.confirm_btnText = confirm_btnText;
            this.confirm_btnClickListener = listener;
            return this;
        }

        /**
         * Set the negative button resource and it's listener
         *
         * @param cancel_btnText
         * @return
         */
        public Builder setNegativeButton(int cancel_btnText,
                                         OnClickListener listener) {
            this.cancel_btnText = (String) context.getText(cancel_btnText);
            this.cancel_btnClickListener = listener;
            return this;
        }

        /**
         * Set the negative button and it's listener
         *
         * @param cancel_btnText
         * @return
         */
        public Builder setNegativeButton(String cancel_btnText,
                                         OnClickListener listener) {
            this.cancel_btnText = cancel_btnText;
            this.cancel_btnClickListener = listener;
            return this;
        }

        /**
         * Set the netural button resource and it's listener
         *
         * @param neutral_btnText
         * @return
         */
        public Builder setNeutralButton(int neutral_btnText,
                                        OnClickListener listener) {
            this.neutral_btnText = (String) context.getText(neutral_btnText);
            this.neutral_btnClickListener = listener;
            return this;
        }

        /**
         * Set the netural button and it's listener
         *
         * @param neutral_btnText
         * @return
         */
        public Builder setNeutralButton(String neutral_btnText,
                                        OnClickListener listener) {
            this.neutral_btnText = neutral_btnText;
            this.neutral_btnClickListener = listener;
            return this;
        }

        public CustomDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final CustomDialog dialog = new CustomDialog(context,
                    R.style.mystyle);
            View layout = inflater.inflate(vertical ? R.layout.cart_dialog_vertical : R.layout.cart_dialog, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // set the dialog title
            TextView titleTv = (TextView) layout.findViewById(R.id.title);
            titleTv.setText(title);
            titleTv.getPaint().setFakeBoldText(true);

            if (title == null || title.trim().length() == 0) {
                ((TextView) layout.findViewById(R.id.message))
                        .setGravity(Gravity.CENTER);
            } else {
                titleTv.setVisibility(View.VISIBLE);
                int top = UIUtils.dip2px(4);
                int bottom = UIUtils.dip2px(10);
                LinearLayout ll = (LinearLayout) layout.findViewById(R.id.layout);
                View messageRoot = ll.getChildAt(1);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) messageRoot.getLayoutParams();
                layoutParams.setMargins(0, top, 0, bottom);
                ll.updateViewLayout(messageRoot, layoutParams);
            }
            if (neutral_btnText != null && confirm_btnText != null
                    && cancel_btnText != null) {
                ((Button) layout.findViewById(R.id.confirm_btn))
                        .setText(confirm_btnText);
                if (neutral_btnClickListener != null) {
                    ((Button) layout.findViewById(R.id.neutral_btn))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    neutral_btnClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEUTRAL);
                                }
                            });
                } else {
                    ((Button) layout.findViewById(R.id.neutral_btn))
                            .setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                }
            } else {
                // if no confirm button or cancle button or neutral just set the
                // visibility to GONE
                layout.findViewById(R.id.neutral_btn).setVisibility(View.GONE);
                layout.findViewById(R.id.single_line).setVisibility(View.GONE);
            }
            // set the confirm button
            if (confirm_btnText != null) {
                ((Button) layout.findViewById(R.id.confirm_btn))
                        .setText(confirm_btnText);
                if (confirm_btnClickListener != null) {
                    ((Button) layout.findViewById(R.id.confirm_btn))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    confirm_btnClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                } else {
                    ((Button) layout.findViewById(R.id.confirm_btn))
                            .setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.confirm_btn).setVisibility(View.GONE);
                layout.findViewById(R.id.second_line).setVisibility(View.GONE);
                layout.findViewById(R.id.cancel_btn).setBackgroundResource(
                        R.drawable.single_btn_select);
            }
            // set the cancel button
            if (cancel_btnText != null) {
                ((Button) layout.findViewById(R.id.cancel_btn))
                        .setText(cancel_btnText);
                if (cancel_btnClickListener != null) {
                    ((Button) layout.findViewById(R.id.cancel_btn))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    cancel_btnClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                } else {
                    ((Button) layout.findViewById(R.id.cancel_btn))
                            .setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                }
            } else {
                // if no cancel button just set the visibility to GONE
                layout.findViewById(R.id.cancel_btn).setVisibility(View.GONE);
                layout.findViewById(R.id.second_line).setVisibility(View.GONE);
                layout.findViewById(R.id.confirm_btn).setBackgroundResource(
                        R.drawable.single_btn_select);
                Button viewById = (Button) layout.findViewById(R.id.confirm_btn);
                //viewById.teac
            }
            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(R.id.message)).setText(message);
            } else if (contentView != null) {
                // if no message set
                // add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.message)).removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.message)).addView(
                        contentView, new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            dialog.setContentView(layout);
            return dialog;
        }

    }
}
