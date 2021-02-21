package daily.quotes.english.work

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import daily.quotes.english.TAG
import java.util.*
import java.util.concurrent.TimeUnit


class WorkManagerPeriodic {

    fun setWork() {
        val hourOfTheDay = 20 // When to run the job
        val minuteOfTheHour = 30 // When to run the job
        val repeatInterval = 1 // In days
        val flexTime: Long = calculateFlex(hourOfTheDay, minuteOfTheHour, repeatInterval)
        val workRequest = PeriodicWorkRequest.Builder(
            NotificationWork::class.java,
            repeatInterval.toLong(), TimeUnit.DAYS,
            flexTime, TimeUnit.MILLISECONDS
        ).build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            TAG,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun calculateFlex(hourOfTheDay: Int, minuteOfTheHour: Int, periodInDays: Int): Long {
        // Initialize the calendar with today and the preferred time to run the job.
        val cal1: Calendar = Calendar.getInstance()
        cal1.set(Calendar.HOUR_OF_DAY, hourOfTheDay)
        cal1.set(Calendar.MINUTE, minuteOfTheHour)
        cal1.set(Calendar.SECOND, 0)

        // Initialize a calendar with now.
        val cal2: Calendar = Calendar.getInstance()
        if (cal2.getTimeInMillis() < cal1.getTimeInMillis()) {
            // Add the worker periodicity.
            cal2.setTimeInMillis(cal2.getTimeInMillis() + TimeUnit.DAYS.toMillis(periodInDays.toLong()))
        }
        val delta: Long = cal2.getTimeInMillis() - cal1.getTimeInMillis()
        return if (delta > PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS) delta else PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS
    }

    /*
    init {
        /*
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .setRequiresStorageNotLow(true)
            .setRequiresDeviceIdle(true)
            .build()
         */

        val periodicWorkRequest = PeriodicWorkRequestBuilder<NotificationWork>(12, TimeUnit.HOURS)
            //.setConstraints()
            .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest)

        Log.i(TAG, "WorkManagerPeriodic is ...")
    }
     */
}