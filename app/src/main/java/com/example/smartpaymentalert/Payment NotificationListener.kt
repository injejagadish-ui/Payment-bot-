package com.example.smartpaymentalert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.speech.tts.TextToSpeech
import androidx.core.app.NotificationCompat
import java.util.Locale
import java.util.regex.Pattern

class PaymentNotificationListener :
    NotificationListenerService(), TextToSpeech.OnInitListener {

    private var speaker: TextToSpeech? = null
    private var speakerReady = false

    private val supportedApps = setOf(
        "com.phonepe.app",
        "com.google.android.apps.nbu.paisa.user",
        "net.one97.paytm",
        "in.org.npci.upiapp"
    )

    override fun onCreate() {
        super.onCreate()

        speaker = TextToSpeech(this, this)
        createNotificationChannel()
    }

    override fun onInit(status: Int) {
        speakerReady = status == TextToSpeech.SUCCESS

        if (speakerReady) {
            speaker?.language = Locale("en", "IN")
        }
    }

    override fun onNotificationPosted(
        sbn: StatusBarNotification
    ) {
        if (sbn.packageName !in supportedApps) return

        val extras = sbn.notification.extras

        val title = extras
            .getCharSequence("android.title")
            ?.toString().orEmpty()

        val body = extras
            .getCharSequence("android.text")
            ?.toString().orEmpty()

        val bigText = extras
            .getCharSequence("android.bigText")
            ?.toString().orEmpty()

        val message = "$title $body $bigText"
        val lower = message.lowercase(Locale.ROOT)

        if (!isIncomingPayment(lower)) return

        val amount = extractAmount(message) ?: return

        showAlert(amount)

        if (speakerReady) {
            speaker?.speak(
                "$amount rupees received. Please verify payment.",
                TextToSpeech.QUEUE_ADD,
                null,
                sbn.key
            )
        }
    }

    private fun isIncomingPayment(text: String): Boolean {
        val failureWords = listOf(
            "failed",
            "declined",
            "unsuccessful",
            "pending"
        )

        if (failureWords.any { it in text }) {
            return false
        }

        val outgoingWords = listOf(
            "you paid",
            "sent to",
            "money sent",
            "payment sent",
            "debited"
        )

        if (outgoingWords.any { it in text }) {
            return false
        }

        val incomingWords = listOf(
            "received",
            "credited",
            "money added",
            "paid you",
            "payment received"
        )

        return incomingWords.any { it in text }
    }

    private fun extractAmount(text: String): String? {
        val pattern = Pattern.compile(
            "(?:₹|Rs\\.?\\s*|INR\\s*)([0-9,]+(?:\\.[0-9]{1,2})?)",
            Pattern.CASE_INSENSITIVE
        )

        val matcher = pattern.matcher(text)

        return if (matcher.find()) {
            matcher.group(1)
        } else {
            null
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "payment_alerts",
                "Payment Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )

            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    private fun showAlert(amount: String) {
        val notification =
            NotificationCompat.Builder(this, "payment_alerts")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Payment notification detected")
                .setContentText("₹$amount — please verify")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        manager.notify(
            System.currentTimeMillis().toInt(),
            notification
        )
    }

    override fun onDestroy() {
        speaker?.stop()
        speaker?.shutdown()
        speaker = null
        super.onDestroy()
    }
    }
