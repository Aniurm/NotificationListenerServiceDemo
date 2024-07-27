package com.example.greetingcard

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import com.example.greetingcard.ui.theme.GreetingCardTheme

class MainActivity : ComponentActivity() {
    private val notificationListener = NotificationListener()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 启动 NotificationListenerService
        startService(Intent(this, NotificationListener::class.java))

        setContent {
            GreetingCardTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val context = LocalContext.current
                    Column(modifier = Modifier.padding(innerPadding)) {
                        NotificationButton(context, modifier = Modifier.padding(innerPadding))
                        SendNotificationButton(context, modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }

        // 检查并请求通知访问权限
        if (!isNotificationServiceEnabled()) {
            requestNotificationPermission()
        }
    }

    private fun isNotificationServiceEnabled(): Boolean {
        val cn = ComponentName(this, NotificationListener::class.java)
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return flat != null && flat.contains(cn.flattenToString())
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Notification access requires API level 22+", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun NotificationButton(context: Context, modifier: Modifier = Modifier) {
    Button(
        onClick = {
            val intent = Intent(context, NotificationListener::class.java)
            context.startService(intent)
            Log.d("NotificationButton", "Button Clicked")
        },
        modifier = modifier.padding(16.dp)
    ) {
        Text(text = "获取通知")
    }
}

@Composable
fun SendNotificationButton(context: Context, modifier: Modifier = Modifier) {
    Button(
        onClick = {
            sendNotification(context)
        },
        modifier = modifier.padding(16.dp)
    ) {
        Text(text = "发送通知")
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationButtonPreview() {
    GreetingCardTheme {
        val context = LocalContext.current
        NotificationButton(context)
        SendNotificationButton(context)
    }
}

private fun sendNotification(context: Context) {
    val notificationId = 1
    val channelId = "my_channel_id"

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "My Channel"
        val descriptionText = "Channel description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
        addNextIntentWithParentStack(intent)
        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("My notification")
        .setContentText("Hello World!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    notificationManager.notify(notificationId, builder.build())
}
