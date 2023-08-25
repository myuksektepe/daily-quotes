package daily.quotes.english.work

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import daily.quotes.english.R
import daily.quotes.english.view.MainActivity

class NotificationWork(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        showNotification(applicationContext)
        return Result.success()
    }

    private fun showNotification(context: Context) {
        val CHANNEL_ID = "New Quote" // The id of the channel.

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
                .setContentTitle(context.getString(R.string.notification_title))
        } else {
            NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentTitle(context.getString(R.string.notification_title))
        }

        mBuilder.setContentIntent(contentIntent)
        mBuilder.setContentText(context.getString(R.string.notification_description))
        mBuilder.setAutoCancel(true)
        mNotificationManager.notify(1, mBuilder.build())
    }
}