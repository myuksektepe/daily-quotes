package daily.quotes.english.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import daily.quotes.english.R
import daily.quotes.english.TAG
import daily.quotes.english.view.MainActivity


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        Log.i(TAG, "onReceive")

        val _when = System.currentTimeMillis()
        val notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent = PendingIntent.getActivity(
            context, 0,
            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )


        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val mNotifyBuilder = NotificationCompat.Builder(
            context
        ).setSmallIcon(R.drawable.quotes_logo)
            .setContentTitle("Alarm Fired")
            .setContentText("Events to be Performed").setSound(alarmSound)
            .setAutoCancel(true).setWhen(_when)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
        notificationManager.notify(10001, mNotifyBuilder.build())

    }
}