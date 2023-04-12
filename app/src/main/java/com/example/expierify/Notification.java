package com.example.expierify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Notification /*extends BroadcastReceiver*/ {

    Integer notificationID = 1;
    String channelID = "Channel1";
    String titleExtra = "Food Expiring Soon";
    String messageExtra = "You have a food item expirying soon. Come check it out";
    String userID;
    int daysBefore = 0;
    Boolean status;
    int hour;
    int min;

    public Notification(Boolean status, int daysBefore, int hour, int min) {
        this.status = status;
        this.daysBefore = daysBefore;
        this.hour = hour;
        this.min = min;
    }

    public Notification(Boolean status) {
        this.status = status;
    }
    public Notification() {

    }

    public String getUserID() {
        return userID;
    }

    public int getDaysBefore() {
        return daysBefore;
    }

    public Boolean getStatus() {
        return status;
    }

    public int getHour() {
        return hour;
    }

    public int getMin() {
        return min;
    }

    public String getChannelID() {
        return channelID;
    }

    public Integer getNotificationID() {
        return notificationID;
    }

    public String getTitleExtra() {
        return titleExtra;
    }

    public String getMessageExtra() {
        return messageExtra;
    }

    public void setNotificationID(Integer notificationID) {
        this.notificationID = notificationID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public void setTitleExtra(String titleExtra) {
        this.titleExtra = titleExtra;
    }

    public void setMessageExtra(String messageExtra) {
        this.messageExtra = messageExtra;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setDaysBefore(int daysBefore) {
        this.daysBefore = daysBefore;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMin(int min) {
        this.min = min;
    }

    /*@Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context,channelID)
                .setSmallIcon(R.drawable.bell)
                .setContentTitle(intent.getStringExtra(titleExtra))
                .setContentText(intent.getStringExtra(messageExtra))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationID, notification.build());






    }*/
}
