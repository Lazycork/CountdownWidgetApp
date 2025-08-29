package com.example.countdownapp
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

data class CountdownItem(val id: Long, val title: String, val targetMillis: Long)

object CountdownStore {
    private const val PREF = "countdown_store"
    private fun prefs(ctx: Context) = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
    fun add(ctx: Context, title: String, targetMillis: Long): Long {
        val id = System.currentTimeMillis()
        val arr = JSONArray(prefs(ctx).getString("items", "[]"))
        val obj = JSONObject(); obj.put("id", id); obj.put("title", title); obj.put("target", targetMillis); arr.put(obj)
        prefs(ctx).edit().putString("items", arr.toString()).apply(); return id
    }
    fun update(ctx: Context, id: Long, title: String, targetMillis: Long) {
        val arr = JSONArray(prefs(ctx).getString("items", "[]"))
        for (i in 0 until arr.length()) { val o = arr.getJSONObject(i); if (o.getLong("id")==id) { o.put("title", title); o.put("target", targetMillis) } }
        prefs(ctx).edit().putString("items", arr.toString()).apply()
    }
    fun delete(ctx: Context, id: Long) {
        val arr = JSONArray(prefs(ctx).getString("items", "[]")); val out = JSONArray()
        for (i in 0 until arr.length()) { val o = arr.getJSONObject(i); if (o.getLong("id")!=id) out.put(o) }
        prefs(ctx).edit().putString("items", out.toString()).apply()
    }
    fun getAll(ctx: Context): List<CountdownItem> {
        val out = ArrayList<CountdownItem>(); val arr = JSONArray(prefs(ctx).getString("items","[]"))
        for (i in 0 until arr.length()) { val o = arr.getJSONObject(i); out.add(CountdownItem(o.getLong("id"), o.getString("title"), o.getLong("target"))) }
        out.sortBy { it.targetMillis }; return out
    }
    fun getById(ctx: Context, id: Long): CountdownItem? {
        val arr = JSONArray(prefs(ctx).getString("items","[]"))
        for (i in 0 until arr.length()) { val o = arr.getJSONObject(i); if (o.getLong("id")==id) return CountdownItem(o.getLong("id"), o.getString("title"), o.getLong("target")) }
        return null
    }
}
