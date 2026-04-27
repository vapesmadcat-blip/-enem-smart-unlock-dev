package com.enem.smartunlock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScreenReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prefs = context.getSharedPreferences("enem_unlock", Context.MODE_PRIVATE)
        val unlockedUntil = prefs.getLong("unlocked_until", 0L)

        if (System.currentTimeMillis() < unlockedUntil) {
            return
        }

        if (
            intent.action == Intent.ACTION_SCREEN_ON ||
            intent.action == Intent.ACTION_BOOT_COMPLETED
        ) {
            val openApp = Intent(context, LockScreenActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }

            context.startActivity(openApp)
        }
    }
}
