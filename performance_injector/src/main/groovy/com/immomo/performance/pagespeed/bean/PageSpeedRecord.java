package com.immomo.performance.pagespeed.bean;


/**
 * Created by tlrk on 9/2/18.
 */
public class PageSpeedRecord {

    // base info
    // momoid
    public String uid;

    public String appVersionName;

    public int appVersionCode;

    public String osVersion;
    // 手机型号

    public String brand;

    // page info

    // 类名称
    public String pageSimpleClass;

    // 页面启动的时间
    public long timestamp;

    // 时间消耗，单位毫秒
    public long costInMilliseconds;

    // 是否是冷启动
    public boolean isColdLaunch = false;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("----> page : ")
                .append(pageSimpleClass).append(" ")
                .append("cost : ").append(costInMilliseconds).append(" ms ");
//                .append(" isColdLaunch : ").append(isColdLaunch)
//                .append(" time : ").append(PageSpeedUtils.convertTime(timestamp));
        return builder.toString();
    }
}
