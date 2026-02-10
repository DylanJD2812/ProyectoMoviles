package com.ACID.geojournal

import Controller.HistoryController
import Entity.History
import Interface.OnItemClickListener
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import androidx.fragment.app.commit

class HistoryListActivity : AppCompatActivity() , OnItemClickListener {
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
        adapter = HistoryAdapter(this)
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
    override fun onItemClicked(history: History) {
        showFragment(HistoryFragment.newInstance(history.Id))
    }
    private fun showFragment(fragment: Fragment) {
        val recycler = findViewById<RecyclerView>(R.id.recyclerHistory)
        val container = findViewById<View>(R.id.fragment_container)

        // mostrar contenedor
        container.visibility = View.VISIBLE

        // 60% recycler, 40% fragment (ajústalo)
        (recycler.layoutParams as LinearLayout.LayoutParams).weight = 0.6f
        (container.layoutParams as LinearLayout.LayoutParams).weight = 0.4f
        recycler.requestLayout()
        container.requestLayout()

        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
            setReorderingAllowed(true)
            addToBackStack("history_detail")
        }
    }
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()

            val recycler = findViewById<RecyclerView>(R.id.recyclerHistory)
            val container = findViewById<View>(R.id.fragment_container)

            (recycler.layoutParams as LinearLayout.LayoutParams).weight = 1f
            (container.layoutParams as LinearLayout.LayoutParams).weight = 0f
            container.visibility = View.GONE
            recycler.requestLayout()
            container.requestLayout()
        } else {
            super.onBackPressed()
        }
    }

}