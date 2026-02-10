package com.ACID.geojournal

import Entity.History
import Interface.OnItemClickListener
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(val clickListener: OnItemClickListener) : RecyclerView.Adapter<HViewHolder>() {

    private val historyList = mutableListOf<History>()

    fun submitList(newList: List<History>) {
        historyList.clear()
        historyList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return HViewHolder(
            layoutInflater.inflate(R.layout.item_history, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HViewHolder, position: Int) {
        holder.render(historyList[position],clickListener)
    }

    override fun getItemCount(): Int = historyList.size
}
