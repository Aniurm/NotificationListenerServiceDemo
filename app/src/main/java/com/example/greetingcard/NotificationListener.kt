package com.example.greetingcard

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationListener : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d("NotificationListener", "Notification listener service connected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        val extras = sbn.notification.extras
        // 获取接收消息APP的包名
        val notificationPkg = sbn.packageName
        // 获取接收消息的抬头
        val notificationTitle = extras.getString(Notification.EXTRA_TITLE)
        // 获取接收消息的内容
        val notificationText = extras.getString(Notification.EXTRA_TEXT)
        Log.d("收到的消息包名", notificationPkg)
        Log.d("收到的消息内容", "Notification posted $notificationTitle & $notificationText")
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        Log.d("NotificationListener", "Notification Removed: ${sbn.notification}")
    }
}
