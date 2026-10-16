package com.example.smartpaymentalert

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 60, 32, 32)
        }

        val title = TextView(this).apply {
            text = "Smart Payment Alert"
            textSize = 26f
        }

        val info = TextView(this).apply {
            text = "Get a voice alert when a supported payment app reports an incoming payment."
            textSize = 16f
            setPadding(0, 24, 0, 24)
        }

        val button = Button(this).apply {
            text = "Enable Notification Access"
            setOnClickListener {
                startActivity(
                    Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                )
            }
        }

        layout.addView(title)
        layout.addView(info)
        layout.addView(button)

        setContentView(layout)
    }
}
