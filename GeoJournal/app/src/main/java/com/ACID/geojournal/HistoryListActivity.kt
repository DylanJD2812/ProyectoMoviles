package com.ACID.geojournal

import Controller.HistoryController
import Entity.History
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class HistoryListActivity : AppCompatActivity() {
    private lateinit var controller: HistoryController
    private lateinit var adapter: HistoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        controller = HistoryController(this)
        initReciclerView()
        val PrsId = Util.Util.personID
        if (PrsId != null){
            loadHistories(PrsId)}
        else{
            Util.Util.showShortToast(this, getString(R.string.MsgDataNotFound))
        }

    }
    fun initReciclerView(){
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerHistory)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = HistoryAdapter()
        recyclerView.adapter = adapter

    }
    private fun loadHistories(personId: String) {
        lifecycleScope.launch {
            try {
                val histories: List<History> = controller.getAllHistoriesByPerson(personId)
                adapter.submitList(histories)
            } catch (e: Exception) {
                Toast.makeText(this@HistoryListActivity, e.message ?: "Error", Toast.LENGTH_LONG).show()
            }
        }
    }
}