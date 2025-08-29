package com.example.countdownapp
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MidnightAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val mgr = AppWidgetManager.getInstance(context)
        val ids = mgr.getAppWidgetIds(android.content.ComponentName(context, CountdownWidget::class.java))
        if (ids.isNotEmpty()) { for (id in ids) CountdownWidget().updateWidget(context, mgr, id) }
    }
}
