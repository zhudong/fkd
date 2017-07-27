package com.fuexpress.kr.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;

import com.fuexpress.kr.R;
import com.fuexpress.kr.ui.uiutils.UIUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Longer on 2016/10/26.
 */
public class CommonUtils {
    /**
     * return if str is empty
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0 || str.equalsIgnoreCase("null") || str.isEmpty() || str.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * get format date
     *
     * @param timemillis
     * @return
     */
    public static String getFormatDate(long timemillis) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timemillis));
    }

    public static String getFormatDate2(long timemillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return simpleDateFormat.format(new Date(timemillis));
    }

    /**
     * decode Unicode string
     *
     * @param s
     * @return
     */
    public static String decodeUnicodeStr(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '\\' && chars[i + 1] == 'u') {
                char cc = 0;
                for (int j = 0; j < 4; j++) {
                    char ch = Character.toLowerCase(chars[i + 2 + j]);
                    if ('0' <= ch && ch <= '9' || 'a' <= ch && ch <= 'f') {
                        cc |= (Character.digit(ch, 16) << (3 - j) * 4);
                    } else {
                        cc = 0;
                        break;
                    }
                }
                if (cc > 0) {
                    i += 5;
                    sb.append(cc);
                    continue;
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * encode Unicode string
     *
     * @param s
     * @return
     */
    public static String encodeUnicodeStr(String s) {
        StringBuilder sb = new StringBuilder(s.length() * 3);
        for (char c : s.toCharArray()) {
            if (c < 256) {
                sb.append(c);
            } else {
                sb.append("\\u");
                sb.append(Character.forDigit((c >>> 12) & 0xf, 16));
                sb.append(Character.forDigit((c >>> 8) & 0xf, 16));
                sb.append(Character.forDigit((c >>> 4) & 0xf, 16));
                sb.append(Character.forDigit((c) & 0xf, 16));
            }
        }
        return sb.toString();
    }

    /**
     * convert time str
     *
     * @param time
     * @return
     */
    public static String convertTime(int time) {

        time /= 1000;
        int minute = time / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }

    /**
     * url is usable
     *
     * @param url
     * @return
     */
    public static boolean isUrlUsable(String url) {
        if (CommonUtils.isEmpty(url)) {
            return false;
        }

        URL urlTemp = null;
        HttpURLConnection connt = null;
        try {
            urlTemp = new URL(url);
            connt = (HttpURLConnection) urlTemp.openConnection();
            connt.setRequestMethod("HEAD");
            int returnCode = connt.getResponseCode();
            if (returnCode == HttpURLConnection.HTTP_OK) {
                return true;
            }
        } catch (Exception e) {
            return false;
        } finally {
            connt.disconnect();
        }
        return false;
    }

    /**
     * is url
     *
     * @param url
     * @return
     */
    public static boolean isUrl(String url) {
        Pattern pattern = Pattern.compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
        return pattern.matcher(url).matches();
    }

    /**
     * get toolbar height
     *
     * @param context
     * @return
     */
    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static boolean checkEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public static String getErrMsg(int ret) {
        String errorMsg = "服务器返回异常码";
        switch (ret) {
            case -1:
                errorMsg = "服务器内部错误";
                break;
            case -2:
                errorMsg = "无效参数";
                break;
            case -3:
                errorMsg = "协议格式错误";
                break;
            case -4:
                errorMsg = "协议编码错误";
                break;
            case -5:
                errorMsg = "协议解码错误";
                break;
            case -6:
                errorMsg = "协议票据无效";
                break;
            case -7:
                errorMsg = "协议加密失败";
                break;
            case -8:
                errorMsg = "协议解密失败";
                break;
            case -9:
                errorMsg = "协议压缩失败";
                break;
            case -10:
                errorMsg = "协议解压失败";
                break;

            case -11:
                errorMsg = "版本号相同";
                break;
            case -12:
                errorMsg = "调用超时";
                break;
            //账号相关
            case -21:
                errorMsg = "账号已经存在";
                break;
            case -22:
                errorMsg = "账号不存在";
                break;
            case -23:
                errorMsg = "密码错误";
                break;
            case -24:
                //第三方账号已与壹时尚账号绑定
                errorMsg = "账号已经绑定";
                break;
            case -25:
                //第三方账号未与壹时尚账号绑定
                errorMsg = "账号未绑定";
                break;
            case -26:
                errorMsg = "第三方授权违法";
                break;
            case -27:
                errorMsg = "昵称已经存在";
                break;
            case -28:
                errorMsg = "验证码错误";
                break;
            case -29:
                errorMsg = "验证码频率限制";
                break;
            //商户相关
            case -31:
                errorMsg = "已经关注商户";
                break;
            case -32:
                errorMsg = "未关注商户";
                break;
            case -33:
                errorMsg = "商户不存在";
                break;
            //优惠券
            case -41:
                errorMsg = "优惠券已经被使用";
                break;
            case -42:
                errorMsg = "无效优惠券";
                break;

            case -51:
                errorMsg = "用户地址不存在";
                break;
            //充值卡相关
            case -61:
                errorMsg = "充值卡不存在";
                break;
            case -62:
                errorMsg = "充值卡无效";
                break;
            case -71:
                errorMsg = "包裹不存在";
                break;
        }
        return errorMsg;
    }

    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }


    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    //    判断微信是否安装
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }

        return false;
    }

    //    安装应用界面
    public static void install(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static String getCorrectUrl(String url) {
        if (url.contains("!")) {
            return url.substring(0, url.indexOf("!"));
        } else {
            return url;
        }
    }

    public static String formatMoenyWidthCommaKrw(int money) {
        NumberFormat nf = NumberFormat.getInstance();
        String ss = nf.format(money);
        return "₩" + ss;
    }

    public static String formatMoenyWidthCommaLocal(float money, String currency_code, Context context) {
        return UIUtils.getCurrency(context, currency_code, money);
    }
}
