package com.yyscamper.teamaccount;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.frontia.Frontia;
import com.baidu.frontia.FrontiaQuery;
import com.baidu.frontia.FrontiaUser;
import com.baidu.frontia.api.FrontiaAuthorization;
import com.baidu.frontia.api.FrontiaAuthorization.MediaType;
import com.baidu.frontia.api.FrontiaAuthorizationListener.AuthorizationListener;
import com.baidu.frontia.api.FrontiaPush;
import com.baidu.frontia.api.FrontiaPushListener.CommonMessageListener;
import com.baidu.frontia.api.FrontiaPushListener.DescribeMessageListener;
import com.baidu.frontia.api.FrontiaPushListener.DescribeMessageResult;
import com.baidu.frontia.api.FrontiaPushListener.ListMessageListener;
import com.baidu.frontia.api.FrontiaPushListener.PushMessageListener;
import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.baidu.frontia.api.FrontiaPushUtil;
import com.baidu.frontia.api.FrontiaPushUtil.MessageContent;
import com.baidu.frontia.api.FrontiaPushUtil.NotificationContent;
import com.baidu.frontia.api.FrontiaPushUtil.Trigger;

public class CloudMessageReceiver extends FrontiaPushMessageReceiver {

        private static String mUserId;
        private static String mChannelId;
    private final static String TAG = "PushMessageReceiver";

    @Override
    public void onBind(Context context, int errorCode, String appid,
                       String userId, String channelId, String requestId) {
        mUserId = userId;
        mChannelId = channelId;
        StringBuffer sb = new StringBuffer();
        sb.append("绑定成功\n");
        sb.append("errCode:"+errorCode);
        sb.append("appid:"+appid+"\n");
        sb.append("userId:"+userId+"\n");
        sb.append("channelId:"+channelId+"\n");
        sb.append("requestId"+requestId+"\n");
        Log.d(TAG,sb.toString());
    }

    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        StringBuffer sb = new StringBuffer();
        sb.append("解绑成功\n");
        sb.append("errCode:"+errorCode);
        sb.append("requestId"+requestId+"\n");
        Log.d(TAG,sb.toString());
    }

    @Override
    public void onSetTags(Context context, int errorCode,
                          List<String> successTags, List<String> failTags,
                          String requestId) {
        StringBuffer sb = new StringBuffer();
        sb.append("设置tag成功\n");
        sb.append("errCode:"+errorCode);
        sb.append("success tags:");
        for(String tag:successTags){
            sb.append(tag+"\n");
        }
        sb.append("fail tags:");
        for(String tag:failTags){
            sb.append(tag+"\n");
        }
        sb.append("requestId"+requestId+"\n");
        Log.d(TAG,sb.toString());
    }

    @Override
    public void onDelTags(Context context, int errorCode,
                          List<String> successTags, List<String> failTags,
                          String requestId) {
        StringBuffer sb = new StringBuffer();
        sb.append("删除tag成功\n");
        sb.append("errCode:"+errorCode);
        sb.append("success tags:");
        for(String tag:successTags){
            sb.append(tag+"\n");
        }
        sb.append("fail tags:");
        for(String tag:failTags){
            sb.append(tag+"\n");
        }
        sb.append("requestId"+requestId+"\n");
        Log.d(TAG,sb.toString());
    }

    @Override
    public void onListTags(Context context, int errorCode,
                           List<String> tags, String requestId) {
        StringBuffer sb = new StringBuffer();
        sb.append("list tag成功\n");
        sb.append("errCode:"+errorCode);
        sb.append("tags:");
        for(String tag:tags){
            sb.append(tag+"\n");
        }
        sb.append("requestId"+requestId+"\n");
        Log.d(TAG,sb.toString());
    }

    @Override
    public void onMessage(Context context, String message,
                          String customContentString) {
        StringBuffer sb = new StringBuffer();
        sb.append("收到消息\n");
        sb.append("内容是:"+customContentString+"\n");
        sb.append("tags:");
        sb.append("message:"+message+"\n");
        Log.d(TAG,sb.toString());
    }

    @Override
    public void onNotificationClicked(Context context, String title,
                                      String description, String customContentString) {
        StringBuffer sb = new StringBuffer();
        sb.append("通知被点击\n");
        sb.append("title:"+title+"\n");
        sb.append("description:"+description);
        sb.append("customContentString:"+customContentString+"\n");
        Log.d(TAG,sb.toString());
    }



}