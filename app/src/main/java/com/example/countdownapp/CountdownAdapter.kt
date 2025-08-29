package com.example.countdownapp
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.countdownapp.databinding.ItemCountdownModernBinding

class CountdownAdapter(private val onEdit:(CountdownItem)->Unit, private val onDelete:(CountdownItem)->Unit) : RecyclerView.Adapter<CountdownAdapter.VH>() {
    private var list: List<CountdownItem> = emptyList()
    fun updateList(newList: List<CountdownItem>) { list = newList; notifyDataSetChanged() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH { val b = ItemCountdownModernBinding.inflate(LayoutInflater.from(parent.context), parent, false); return VH(b) }
    override fun onBindViewHolder(holder: VH, position: Int) { holder.bind(list[position]) }
    override fun getItemCount(): Int = list.size
    inner class VH(private val b: ItemCountdownModernBinding): RecyclerView.ViewHolder(b.root) {
        fun bind(item: CountdownItem) {
            b.title.text = item.title; b.subtitle.text = Utils.formatDateTime(item.targetMillis)
            val now = System.currentTimeMillis(); val diff = kotlin.math.max(0L, item.targetMillis - now)
            val days = diff / (1000L*60*60*24); val hours = (diff / (1000L*60*60)) % 24; val minutes = (diff/(1000L*60))%60
            b.remaining.text = "${'$'}{days}d ${'$'}{hours}h ${'$'}{minutes}m"
            b.editBtn.setOnClickListener{ onEdit(item) }; b.deleteBtn.setOnClickListener{ onDelete(item) }
        }
    }
}
