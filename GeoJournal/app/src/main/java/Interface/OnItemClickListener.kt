package Interface

import Entity.History

interface OnItemClickListener {
    fun onItemClicked(history: History)
}