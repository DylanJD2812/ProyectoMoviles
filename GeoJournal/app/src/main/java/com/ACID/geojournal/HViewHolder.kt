package com.ACID.geojournal

import Entity.History
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val title = view.findViewById<TextView>(R.id.item_Title)
    val description = view.findViewById<TextView>(R.id.item_Description)
    val location = view.findViewById<TextView>(R.id.item_Location)

    fun render(history: History){
        title.text = history.Title
        description.text = history.Comment
        location.text = history.Location
    }
}