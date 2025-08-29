package com.example.countdownapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.countdownapp.databinding.ActivityAddEditBinding
import java.util.*

class AddEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEditBinding
    private var editingId: Long = -1L
    private var targetMillis: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editingId = intent.getLongExtra("id", -1L)
        if (editingId != -1L) {
            val item = CountdownStore.getById(this, editingId)
            item?.let {
                binding.titleEdit.setText(it.title)
                targetMillis = it.targetMillis
                binding.dateText.text = Utils.formatDateTime(targetMillis)
            }
        } else {
            targetMillis = System.currentTimeMillis() + 24*3600*1000
            binding.dateText.text = Utils.formatDateTime(targetMillis)
        }

        binding.pickDate.setOnClickListener {
            val cal = Calendar.getInstance().apply { timeInMillis = targetMillis }
            DatePickerDialog(this, { _, y, m, d ->
                cal.set(Calendar.YEAR, y); cal.set(Calendar.MONTH, m); cal.set(Calendar.DAY_OF_MONTH, d)
                TimePickerDialog(this, { _, hour, minute ->
                    cal.set(Calendar.HOUR_OF_DAY, hour); cal.set(Calendar.MINUTE, minute); cal.set(Calendar.SECOND,0)
                    targetMillis = cal.timeInMillis
                    binding.dateText.text = Utils.formatDateTime(targetMillis)
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.saveButton.setOnClickListener {
            val title = binding.titleEdit.text.toString().ifBlank { "Event" }
            if (editingId == -1L) {
                CountdownStore.add(this, title, targetMillis)
            } else {
                CountdownStore.update(this, editingId, title, targetMillis)
                val mgr = android.appwidget.AppWidgetManager.getInstance(this)
                val ids = mgr.getAppWidgetIds(android.content.ComponentName(this, CountdownWidget::class.java))
                for (id in ids) {
                    val pref = WidgetPrefs.loadForWidget(this, id)
                    if (pref != null && pref.eventId == editingId) {
                        CountdownWidget().updateWidget(this, mgr, id)
                    }
                }
            }
            MidnightAlarmScheduler.scheduleNext(this)
            finish()
        }
    }
}
