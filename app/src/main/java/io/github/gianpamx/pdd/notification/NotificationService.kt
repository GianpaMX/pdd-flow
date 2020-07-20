package io.github.gianpamx.pdd.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import io.github.gianpamx.pdd.MainActivity
import io.github.gianpamx.pdd.R
import io.github.gianpamx.pdd.app.ComponentApp
import io.github.gianpamx.pdd.notification.NotificationCommand.HIDE
import io.github.gianpamx.pdd.notification.NotificationCommand.SHOW
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

const val NOTIFICATION_SERVICE_COMMAND = "NOTIFICATION_SERVICE_COMMAND"

private const val NOTIFICATION_ID = 1
private const val CHANNEL_ID = "CLOCK_CHANNEL"


enum class NotificationCommand {
    SHOW,
    HIDE
}

class NotificationService : LifecycleService() {
    @Inject
    lateinit var viewModel: NotificationViewModel

    override fun onCreate() {
        super.onCreate()

        val app = application as ComponentApp
        app.component.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val command = intent?.extras?.let { extras ->
            extras.getString(NOTIFICATION_SERVICE_COMMAND)?.let { NotificationCommand.valueOf(it) }
        } ?: throw IllegalCommandException

        when (command) {
            SHOW -> showNotification()
            HIDE -> hideNotification()
        }

        return START_NOT_STICKY
    }

    private fun showNotification() {
        val pendingIntent = Intent(this, MainActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, 0)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) registerChannel()

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_timelapse_24)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)

        viewModel.errors
            .onEach { Timber.e(it) }
            .launchIn(lifecycleScope)

        viewModel.notificationState.observe(this, Observer {
            with(NotificationManagerCompat.from(this)) {
                notificationBuilder.setContentTitle(it.clock)
                notify(NOTIFICATION_ID, notificationBuilder.build())
            }
        })

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun registerChannel() {
        val name = getString(R.string.channel_name)

        val channel = NotificationChannel(CHANNEL_ID, name, IMPORTANCE_LOW).apply {
            description = getString(R.string.channel_description)
        }

        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).run {
            createNotificationChannel(channel)
        }
    }

    private fun hideNotification() {
        stopForeground(true)
        stopSelf()
    }
}

object IllegalCommandException : IllegalArgumentException()
