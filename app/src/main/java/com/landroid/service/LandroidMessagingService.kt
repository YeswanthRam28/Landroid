// app/src/main/java/com/landroid/service/LandroidMessagingService.kt
package com.landroid.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.landroid.MainActivity
import com.landroid.core.security.TokenManager
import com.landroid.features.alerts.data.db.AlertDatabase
import com.landroid.shared.models.Alert
import com.landroid.shared.models.AlertCategory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class LandroidMessagingService : FirebaseMessagingService() {

    @Inject lateinit var tokenManager: TokenManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val CHANNEL_ID = "landroid_alerts"
        private const val CHANNEL_NAME = "Land Alerts"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        tokenManager.saveFcmToken(token)
        // In production: update FCM token in Supabase users table
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        val title = data["title"] ?: message.notification?.title ?: "Land Alert"
        val body = data["body"] ?: message.notification?.body ?: ""
        val parcelId = data["parcelId"] ?: ""
        val categoryStr = data["category"] ?: "INSIGHT"

        val category = try {
            AlertCategory.valueOf(categoryStr.uppercase())
        } catch (e: IllegalArgumentException) {
            AlertCategory.INSIGHT
        }

        val alert = Alert(
            id = UUID.randomUUID().toString(),
            parcelId = parcelId,
            title = title,
            description = body,
            category = category,
            isRead = false,
            timestamp = System.currentTimeMillis()
        )

        // Insert into Room on IO dispatcher
        serviceScope.launch {
            runCatching {
                AlertDatabase.getInstance(applicationContext).alertDao().insert(alert)
            }
        }

        // Post system notification
        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        runCatching {
            NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Landroid alerts for land health and boundary events"
            }
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.coroutineContext[kotlinx.coroutines.Job]?.cancel()
    }
}
