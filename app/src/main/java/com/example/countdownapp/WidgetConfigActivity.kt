package com.example.countdownapp

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.countdownapp.databinding.ActivityWidgetConfigBinding
import java.util.*

class WidgetConfigActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWidgetConfigBinding
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private var creatingNew = false
    private var tempTargetMillis: Long = System.currentTimeMillis() + 24*3600*1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWidgetConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent; val extras = intent.extras
        if (extras != null) appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) { finish(); return }

        val list = CountdownStore.getAll(this)
        val titles = mutableListOf<String>()
        for (it in list) titles.add("${'$'}{it.title} — ${'$'}{Utils.formatDateTime(it.targetMillis)}")
        titles.add(0, "Create new event...")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, titles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.countdownPicker.adapter = adapter

        binding.countdownPicker.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                creatingNew = position == 0
                binding.newContainer.visibility = if (creatingNew) View.VISIBLE else View.GONE
                if (!creatingNew) {
                    val sel = list[position-1]
                    binding.selectedLabel.text = "Using: ${'$'}{sel.title} — ${'$'}{Utils.formatDateTime(sel.targetMillis)}"
                } else {
                    binding.newTitle.setText(""); tempTargetMillis = System.currentTimeMillis()+24*3600*1000L; binding.newDate.text = Utils.formatDateTime(tempTargetMillis)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.pickNewDate.setOnClickListener {
            val cal = Calendar.getInstance().apply { timeInMillis = tempTargetMillis }
            DatePickerDialog(this, { _, y, m, d ->
                cal.set(Calendar.YEAR, y); cal.set(Calendar.MONTH, m); cal.set(Calendar.DAY_OF_MONTH, d)
                TimePickerDialog(this, { _, hour, minute ->
                    cal.set(Calendar.HOUR_OF_DAY, hour); cal.set(Calendar.MINUTE, minute); cal.set(Calendar.SECOND,0)
                    tempTargetMillis = cal.timeInMillis; binding.newDate.text = Utils.formatDateTime(tempTargetMillis)
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.saveButton.setOnClickListener {
            val listAll = CountdownStore.getAll(this)
            val index = binding.countdownPicker.selectedItemPosition
            val eventId: Long = if (index == 0) {
                val title = binding.newTitle.text.toString().ifBlank { "Event" }
                CountdownStore.add(this, title, tempTargetMillis)
            } else {
                listAll[index-1].id
            }
            WidgetPrefs.saveForWidget(this, appWidgetId, eventId)
            val mgr = AppWidgetManager.getInstance(this)
            CountdownWidget().updateWidget(this, mgr, appWidgetId)
            val res = Intent(); res.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(Activity.RESULT_OK, res); finish()
        }
    }
}
