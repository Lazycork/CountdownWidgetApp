package com.example.countdownapp
import android.content.Context
data class WidgetPref(val eventId: Long)
object WidgetPrefs {
    private const val PREF = "widget_prefs"
    private fun prefs(ctx: Context) = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
    fun saveForWidget(ctx: Context, widgetId: Int, eventId: Long) { prefs(ctx).edit().putLong(widgetId.toString(), eventId).apply() }
    fun loadForWidget(ctx: Context, widgetId: Int): WidgetPref? { if (!prefs(ctx).contains(widgetId.toString())) return null; val id = prefs(ctx).getLong(widgetId.toString(), -1L); if (id==-1L) return null; return WidgetPref(id) }
    fun remove(ctx: Context, widgetId: Int) { prefs(ctx).edit().remove(widgetId.toString()).apply() }
    fun removeWidgetsForEvent(ctx: Context, eventId: Long) { val p = prefs(ctx); for ((k,_) in p.all) { try { val wid = k.toInt(); val ev = p.getLong(k,-1L); if (ev==eventId) p.edit().remove(k).apply() } catch(_:Exception){} } }
}
