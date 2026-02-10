package com.ACID.geojournal

import Controller.HistoryController
import Entity.History
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private lateinit var controller: HistoryController

    private var titleEdit: TextInputEditText? = null
    private var commentEdit: TextInputEditText? = null
    private var currentHistory: History? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller = HistoryController(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_history, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleEdit = view.findViewById(R.id.editTitle)
        commentEdit = view.findViewById(R.id.editComment)
        val btnSave = view.findViewById<View>(R.id.btnSaveChanges)
        btnSave.setOnClickListener {
            updateHistory()
        }

        val personId = Util.Util.personID
        val historyId = arguments?.getString(ARG_HISTORY_ID)

        if (personId.isNullOrBlank() || historyId.isNullOrBlank()) return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val history = controller.getHistoryById(personId, historyId)
                titleEdit?.setText(history?.Title.orEmpty())
                commentEdit?.setText(history?.Comment.orEmpty())
                currentHistory = history
            } catch (_: Exception) { }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        titleEdit = null
        commentEdit = null
    }

    companion object {
        private const val ARG_HISTORY_ID = "history_id"

        fun newInstance(historyId: String) = HistoryFragment().apply {
            arguments = Bundle().apply { putString(ARG_HISTORY_ID, historyId) }
        }
    }
    private fun updateHistory() {
        val personId = Util.Util.personID ?: return
        val history = currentHistory ?: return

        // tomar valores del UI
        val newTitle = titleEdit?.text?.toString()?.trim().orEmpty()
        val newComment = commentEdit?.text?.toString()?.trim().orEmpty()

        // 🔥 actualizar el MISMO objeto
        history.Title = newTitle
        history.Comment = newComment
        // history.Location = ...
        // history.Photo = ...

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                controller.updateHistory(personId, history)
                // opcional: Toast "Guardado"
            } catch (e: Exception) {
                // opcional: Toast de error
            }
        }
    }

}
