package com.fuexpress.kr.ui.view;

/**
 * Created by Administrator on 2017-4-17.
 */


import adyen.com.adyenpaysdk.Adyen;
import adyen.com.adyenpaysdk.util.CardType;
import adyen.com.adyenuisdk.customcomponents.AdyenEditText;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.InputFilter.LengthFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView.ScaleType;
import android.widget.ViewSwitcher.ViewFactory;

import com.fuexpress.kr.R;
import com.fuexpress.kr.ui.activity.MyPayActivity;
import com.fuexpress.kr.utils.LogUtils;

import java.util.Calendar;

public class CreditCardForm extends LinearLayout {
    private AdyenEditText mCreditCardNo;
    private AdyenEditText mCreditCardExpDate;
    private AdyenEditText mCreditCardCvc;
    private ImageSwitcher mCardType;
    private boolean keyDel;
    private static boolean mValidCardNumber = false;
    public static boolean mValidExpiryDate = false;
    private static boolean mValidCvc = false;

    public CreditCardForm(Context context) {
        super(context);
        this.keyDel = false;
        LayoutInflater.from(context).inflate(R.layout.payment_form, this);
    }

    public CreditCardForm(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.keyDel = false;
        LayoutInflater.from(context).inflate(R.layout.credit_card_form, this);
        this.initViews(context, attrs);
    }

    public CreditCardForm(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
        this.initViews(context, attrs);
    }

    private void initViews(final Context context, AttributeSet attrs) {
        this.mCreditCardNo = (AdyenEditText) this.findViewById(R.id.credit_card_no);
//        this.mCreditCardNo.requestFocus();
        this.mCreditCardExpDate = (AdyenEditText) this.findViewById(R.id.credit_card_exp_date);
        this.mCreditCardCvc = (AdyenEditText) this.findViewById(R.id.credit_card_cvc);
        this.mCardType = (ImageSwitcher) this.findViewById(R.id.cardType);
        this.mCardType.setFactory(new ViewFactory() {
            public View makeView() {
                ImageView myImageView = new ImageView(context);
                myImageView.setScaleType(ScaleType.FIT_CENTER);
                myImageView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
                myImageView.setImageResource(R.mipmap.ady_card_unknown);
                myImageView.setTag(Integer.valueOf(R.mipmap.ady_card_unknown));
                return myImageView;
            }
        });
        this.mCardType.setInAnimation(AnimationUtils.loadAnimation(context, adyen.com.adyenpaysdk.R.anim.abc_fade_in));
        this.mCardType.setOutAnimation(AnimationUtils.loadAnimation(context, adyen.com.adyenpaysdk.R.anim.abc_fade_out));
        this.initFormStyle();
        this.initCreditCardEditText();
        this.initExpDateEditText();
        this.initCvcText();
    }

    private void initFormStyle() {
        this.mCreditCardNo.getBackground().setColorFilter(this.getResources().getColor(R.color.light_grey_border), Mode.SRC_ATOP);
        this.mCreditCardExpDate.getBackground().setColorFilter(this.getResources().getColor(R.color.light_grey_border), Mode.SRC_ATOP);
        this.mCreditCardCvc.getBackground().setColorFilter(this.getResources().getColor(R.color.light_grey_border), Mode.SRC_ATOP);
    }

    private void initCreditCardEditText() {
//        this.mCreditCardNo.setFilters(new InputFilter[]{new InputFilter() {
//            public CharSequence filter(CharSequence cs, int start, int end, Spanned spanned, int dStart, int dEnd) {
//                return (CharSequence)(cs.equals("")?cs:(cs.toString().matches("[0-9 ]+")?cs:""));
//            }
//        }});
        this.mCreditCardNo.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    CreditCardForm.this.setCardImageByType(s.toString());
                }

                char c;
                if (s.length() > 0 && s.length() % 5 == 0) {
                    c = s.charAt(s.length() - 1);
                    if (32 == c) {
                        s.delete(s.length() - 1, s.length());
                    }
                }

                if (s.length() > 0 && s.length() % 5 == 0) {
                    c = s.charAt(s.length() - 1);
                    if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(' ')).length <= 3) {
                        s.insert(s.length() - 1, String.valueOf(' '));
                    }
                }

            }
        });
        this.mCreditCardNo.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i("OnFocusChange: ", String.valueOf(hasFocus));
                String creditCardNumber = CreditCardForm.this.mCreditCardNo.getText().toString();
                if (!TextUtils.isEmpty(creditCardNumber) && !Adyen.getInstance().luhnCheck(creditCardNumber.toString()) && !hasFocus) {
                    CreditCardForm.this.mCreditCardNo.setTextColor(CreditCardForm.this.getResources().getColor(R.color.red));
                    CreditCardForm.mValidCardNumber = false;
                } else if (Adyen.getInstance().luhnCheck(creditCardNumber.toString()) || hasFocus) {
                    CreditCardForm.this.mCreditCardNo.setTextColor(CreditCardForm.this.getResources().getColor(R.color.black));
                    CreditCardForm.mValidCardNumber = true;
                }

            }
        });
    }

    private void initExpDateEditText() {
        this.mCreditCardExpDate.setFilters(new InputFilter[]{new LengthFilter(5)});
        this.mCreditCardExpDate.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before == 1) {
                    CreditCardForm.this.keyDel = true;
                } else {
                    CreditCardForm.this.keyDel = false;
                }

            }

            public void afterTextChanged(Editable s) {
                if (!CreditCardForm.this.keyDel) {
                    if (s.length() == 1 && Integer.valueOf(s.toString()).intValue() > 1) {
                        s.insert(0, "0");
                    }

                    if (s.length() == 2 && Integer.valueOf(s.toString()).intValue() > 12) {
                        s.delete(0, 2);
                    } else if (s.length() == 2) {
                        s.insert(2, "/");
                    }
                }

                if (s.toString().indexOf("/") < 0 && s.length() > 2) {
                    s.insert(2, "/");
                }

                if (!MyPayActivity.isSaved) {

                    if (s.length() == 5 && !MyPayActivity.isSaved) {
                        if (!CreditCardForm.this.validDate()) {
                            CreditCardForm.this.mCreditCardExpDate.setTextColor(CreditCardForm.this.getResources().getColor(R.color.red));
                            CreditCardForm.this.mValidExpiryDate = false;
                        } else {
                            CreditCardForm.this.mCreditCardExpDate.setTextColor(CreditCardForm.this.getResources().getColor(R.color.black));
                            CreditCardForm.this.mValidExpiryDate = true;
                        }
                    } else {
                        CreditCardForm.this.mCreditCardExpDate.setTextColor(CreditCardForm.this.getResources().getColor(R.color.black));
                    }
                }

            }
        });
    }

    public void initCvcText() {
        this.mCreditCardCvc.setFilters(new InputFilter[]{new LengthFilter(3)});
        this.mCreditCardCvc.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (s.length() == 3) {
                    CreditCardForm.this.mValidCvc = true;
                } else {
                    CreditCardForm.this.mValidCvc = false;
                }

            }
        });
    }

    private boolean validDate() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int filledInMonth = Integer.valueOf(this.mCreditCardExpDate.getText().toString().split("/")[0]).intValue();
        int filledInYear = Integer.valueOf("20" + this.mCreditCardExpDate.getText().toString().split("/")[1]).intValue();
        return filledInYear < year ? false : (filledInYear == year && filledInMonth < month ? false : (filledInYear == year && filledInMonth > month ? true : filledInYear > year));
    }

    private void setCardImageByType(String cardNumber) {
        CardType cardType = CardType.detect(cardNumber.replaceAll(" ", ""));
        LogUtils.d(cardType.ordinal() + "");
        if (MyPayActivity.isSaved) {
            return;
        }
        switch (cardType.ordinal()) {
            case 0:
                this.mCardType.setImageResource(R.mipmap.ady_card_amex);
                this.mCardType.setTag(Integer.valueOf(R.mipmap.ady_card_amex));
                break;
            case 1:
                this.mCardType.setImageResource(R.mipmap.ady_card_diners);
                this.mCardType.setTag(Integer.valueOf(R.mipmap.ady_card_diners));
                break;
            case 2:
                this.mCardType.setImageResource(R.mipmap.ady_card_discover);
                this.mCardType.setTag(Integer.valueOf(R.mipmap.ady_card_discover));
                break;
            case 3:
                this.mCardType.setImageResource(R.mipmap.ady_card_elo);
                this.mCardType.setTag(Integer.valueOf(R.mipmap.ady_card_elo));
                break;
            case 4:
                this.mCardType.setImageResource(R.mipmap.ady_card_hipercard);
                this.mCardType.setTag(Integer.valueOf(R.mipmap.ady_card_hipercard));
                break;
            case 5:
                this.mCardType.setImageResource(R.mipmap.ady_card_jcb);
                this.mCardType.setTag(Integer.valueOf(R.mipmap.ady_card_jcb));
                break;
            case 6:
                this.mCardType.setImageResource(R.mipmap.ady_card_visa);
                this.mCardType.setTag(Integer.valueOf(R.mipmap.ady_card_visa));
                break;
            case 7:
                this.mCardType.setImageResource(R.mipmap.ady_card_mastercard);
                this.mCardType.setTag(Integer.valueOf(R.mipmap.ady_card_mastercard));
                break;
            case 8:
                this.mCardType.setImageResource(R.mipmap.ady_card_unionpay);
                this.mCardType.setTag(Integer.valueOf(R.mipmap.ady_card_unionpay));
                break;
            case 9:
            default:
                this.mCardType.setImageResource(R.mipmap.ady_card_unknown);
                break;
        }

    }

    public static boolean isValid() {
        return mValidCardNumber && mValidExpiryDate && mValidCvc;
    }
}
