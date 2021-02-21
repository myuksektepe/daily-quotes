package daily.quotes.english.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import daily.quotes.english.R
import daily.quotes.english.TAG
import daily.quotes.english.view.MainActivity


class AlarmBroadcastReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent?) {
        Log.i(TAG,"AlarmBroadcastReceiver onReceive")
        showNotification(context);
    }

    fun showNotification(context: Context){
        val CHANNEL_ID = "your_name" // The id of the channel.

        val name: CharSequence = context.getResources().getString(R.string.app_name) // The user-visible name of the channel.

        val mBuilder: NotificationCompat.Builder
        val notificationIntent = Intent(context, MainActivity::class.java)
        val bundle = Bundle()
        notificationIntent.putExtras(bundle)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        val contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val mNotificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mBuilder = if (Build.VERSION.SDK_INT >= 26) {
            val mChannel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH)
            mNotificationManager.createNotificationChannel(mChannel)
            NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLights(Color.RED, 300, 300)
                .setChannelId(CHANNEL_ID)
                .setContentTitle("Title")
        } else {
            NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentTitle("Title")
        }

        mBuilder.setContentIntent(contentIntent)
        mBuilder.setContentText("Your Text")
        mBuilder.setAutoCancel(true)
        mNotificationManager.notify(1, mBuilder.build())
    }
}