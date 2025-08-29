package com.example.countdownapp

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.RemoteViews
import kotlin.math.max

class CountdownWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (appWidgetId in appWidgetIds) updateWidget(context, appWidgetManager, appWidgetId)
    }

    fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_countdown)
        val pref = WidgetPrefs.loadForWidget(context, appWidgetId)
        if (pref == null) {
            views.setTextViewText(R.id.widget_title, "No event")
            views.setTextViewText(R.id.widget_days, "0d")
            views.setChronometer(R.id.widget_chronometer, SystemClock.elapsedRealtime(), "%s", true)
        } else {
            val item = CountdownStore.getById(context, pref.eventId)
            if (item == null) {
                views.setTextViewText(R.id.widget_title, "Event removed")
                views.setTextViewText(R.id.widget_days, "0d")
                views.setChronometer(R.id.widget_chronometer, SystemClock.elapsedRealtime(), "%s", true)
            } else {
                views.setTextViewText(R.id.widget_title, item.title)
                val now = System.currentTimeMillis()
                val diff = max(0L, item.targetMillis - now)
                val days = diff / (1000L * 60 * 60 * 24)
                val remainder = diff % (1000L * 60 * 60 * 24)
                val hours = remainder / (1000L * 60 * 60)
                val minutes = (remainder / (1000L * 60)) % 60
                val seconds = (remainder / 1000L) % 60
                views.setTextViewText(R.id.widget_days, "${'$'}{days}d")
                val baseRemainderMillis = (hours * 3600 + minutes * 60 + seconds) * 1000L
                val base = SystemClock.elapsedRealtime() + baseRemainderMillis
                views.setChronometer(R.id.widget_chronometer, base, "%s", true)
                val intent = Intent(context, MainActivity::class.java)
                val pending = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                views.setOnClickPendingIntent(R.id.widget_root, pending)
            }
        }
        appWidgetManager.updateAppWidget(appWidgetId, views)
        MidnightAlarmScheduler.scheduleNext(context)
    }
}
