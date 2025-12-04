package com.ACID.geojournal

import Controller.HistoryController
import Entity.History
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import Util.Util
import java.time.LocalDateTime

class HistoryActivity : AppCompatActivity() {
    private lateinit var btnUpload: Button
    private lateinit var btnSave: Button
    private lateinit var btnClear: Button
    private lateinit var tile: EditText
    private lateinit var comment: EditText
    private lateinit var photo: ImageView
    private var selectedBitmap: Bitmap? = null

    private lateinit var historyController: HistoryController

    // Photo selection launchers
    private lateinit var cameraLauncher: Any
    private lateinit var galleryLauncher: Any

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        historyController = HistoryController(this)
        initializeViews()
        setupPhotoLaunchers()
        setupClickListeners()
    }

    private fun initializeViews() {
        btnUpload = findViewById(R.id.btnUpload_history)
        btnSave = findViewById(R.id.btnSave_history)
        btnClear = findViewById(R.id.btnClear_history)
        tile = findViewById(R.id.TitleTxt_history)
        comment = findViewById(R.id.CommentTxt_history)
        photo = findViewById(R.id.PreviewPhoto_history)
    }

    private fun setupPhotoLaunchers() {
        // Initialize camera launcher
        cameraLauncher = Util.createCameraLauncher(
            activity = this,
            onPhotoCaptured = { bitmap ->
                selectedBitmap = bitmap
                photo.setImageBitmap(bitmap)
                Util.showShortToast(this,"Photo captured successfully")
            },
            onCancel = {
                Util.showShortToast(this,"Photo capture cancelled")
            }
        )

        // Initialize gallery launcher
        galleryLauncher = Util.createGalleryLauncher(
            activity = this,
            onPhotoSelected = { bitmap ->
                selectedBitmap = bitmap
                photo.setImageBitmap(bitmap)
                Util.showShortToast(this,"Photo selected successfully")
            },
            onError = { errorMessage ->
                Util.showShortToast(this,errorMessage)
            },
            onCancel = {
                Util.showShortToast(this,"Photo selection cancelled")
            }
        )
    }

    private fun setupClickListeners() {
        btnUpload.setOnClickListener {
            showPhotoSelectionDialog()
        }
        btnSave.setOnClickListener {
            saveHistory()
        }
        btnClear.setOnClickListener {
            cleanImage()
        }
    }

    private fun showPhotoSelectionDialog() {
        Util.showPhotoSelectionDialog(
            context = this,
            onTakePhoto = {
                Util.takePhoto(cameraLauncher)
            },
            onSelectFromGallery = {
                Util.selectPhoto(galleryLauncher)
            }
        )
    }

    private fun cleanImage() {
        photo.setImageResource(android.R.color.transparent)
        selectedBitmap = null
        Util.showShortToast(this,"Photo cleared")
    }
    private fun validateHistoryInput(): Boolean {
        if (tile.text.trim().isEmpty()) {
            Util.showShortToast(this, "Please enter a title")
            return false
        }
        if (comment.text.trim().isEmpty()) {
            Util.showShortToast(this, "Please enter a comment")
            return false
        }
        return true
    }
    private fun saveHistory() {
        val personId = intent.getStringExtra("PERSON_ID") ?: run {
            Util.showShortToast(this,"Error: User not identified")
            return
        }
        lifecycleScope.launch {
            try {
                if (!validateHistoryInput()) {
                    return@launch
                }
                val history = History().apply {
                    Title = tile.text.toString().trim()
                    Comment = comment.text.toString().trim()
                    Photo = selectedBitmap
                    CreatedAt = LocalDateTime.now()
                    PersonId = personId
                }

                val historyId = historyController.addHistory(personId, history)
                Util.showShortToast(this@HistoryActivity,"History saved successfully!")
                clearForm()

            } catch (e: Exception) {
                Util.showShortToast(this@HistoryActivity,"Error saving history: ${e.message}")
            }
        }
    }

    private fun clearForm() {
        tile.setText("")
        comment.setText("")
        cleanImage()
    }

}
