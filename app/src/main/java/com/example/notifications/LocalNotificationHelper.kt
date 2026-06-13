package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity

class LocalNotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "elmorsy_notifications"
        const val CHANNEL_NAME = "قنوات حجز ومتابعة المرسي"
        const val CHANNEL_DESC = "قناة إرسال تأكيدات حجز خدمات الرعاية الصحية المنزلية وحالة وصول الممرض"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(title: String, message: String, notificationId: Int = (1000..9999).random()) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Stable system drawable across all Android devices
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    // Booking Confirmation push notification helper
    fun sendBookingConfirmation(serviceName: String, nurseName: String, date: String, slot: String) {
        val title = "📅 تم تأكيد طلب الرعاية الطبية"
        val message = "تم جدولة الخدمة ($serviceName) بنجاح مع $nurseName في موعد $date ($slot). شكراً لثقتكم بالمرسي."
        sendNotification(title, message, 101)
    }

    // Reminder visit push notification helper
    fun sendUpcomingVisitReminder(serviceName: String, nurseName: String, slot: String) {
        val title = "🔔 تذكير: اقتراب موعد زيارتك الطبية"
        val message = "نذكركم بموعد زيارة ($serviceName) اليوم مع $nurseName في موعد $slot. يرجى تهيئة المريض للزيارة."
        sendNotification(title, message, 102)
    }

    // Tracking progress push notification helper
    fun sendNurseStatusUpdate(status: String, nurseName: String) {
        val title = when (status) {
            "ON_THE_WAY" -> "🚗 الممرض في الطريق إليكم"
            "ARRIVED" -> "🏠 الممرض وصل إلى موقعكم"
            "IN_PROGRESS" -> "🩺 بدأت جلسة الرعاية الطبية الآن"
            "COMPLETED" -> "✅ اكتملت جلسة الرعاية بنجاح"
            "CANCELLED" -> "❌ تم إلغاء طلب الزيارة"
            else -> "✨ تحديث لحالة حجزكم"
        }
        val message = when (status) {
            "ON_THE_WAY" -> "الممرض $nurseName في طريقه إليكم الآن. يمكنكم مراقبة حركته على الخريطة التفاعلية."
            "ARRIVED" -> "وصل الممرض $nurseName إلى العنوان المحدد بنجاح. يرجى الاستعداد واستقبل الممرض."
            "IN_PROGRESS" -> "يقوم الممرض $nurseName بتقديم الخدمة الطبية بتركيز وأمان الآن."
            "COMPLETED" -> "تم الانتهاء من تقديم الجلسة الطبية. نرجو لكم دوام الصحة والعافية وسنكون ممتنين لتقييم الخدمة."
            "CANCELLED" -> "نأسف، تم إلغاء موعدكم الطبي مع $nurseName بنجاح. تم استرداد رسوم الحجز لمحفظتكم."
            else -> "حدث تحديث جديد لزيارة $nurseName."
        }
        sendNotification(title, message, 103)
    }
}
