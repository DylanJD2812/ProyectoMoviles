
package com.ACID.geojournal

import Controller.PersonController
import Entity.Person
import Util.Util
import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar

class RegisterActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private lateinit var txtName: EditText
    private lateinit var txtLastName: EditText
    private lateinit var txtPhone: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var lbBirthdate: TextView
    private lateinit var btnSignUp: Button
    private lateinit var btnCancel: ImageButton
    private lateinit var btnSelectDate: ImageButton
    private lateinit var btnSelectPhoto: ImageButton // Single button for photo selection

    private var day: Int = 0
    private var month: Int = 0
    private var year: Int = 0

    private lateinit var personController: PersonController

    private lateinit var cameraLauncher: Any
    private lateinit var galleryLauncher: Any
    private lateinit var userPhoto: ImageView
    private var selectedBitmap: Bitmap? = null // Store the selected bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        personController = PersonController(this)
        initializeViews()
        setupPhotoLaunchers()
        setupClickListeners()
        ResetDate()
    }

    private fun initializeViews() {
        txtName = findViewById<EditText>(R.id.nameText)
        txtLastName = findViewById<EditText>(R.id.lnameText)
        txtPhone = findViewById<EditText>(R.id.phoneText)
        txtEmail = findViewById<EditText>(R.id.emailText)
        txtPassword = findViewById<EditText>(R.id.passText)
        lbBirthdate = findViewById<TextView>(R.id.lbBirthdate_Person)
        userPhoto = findViewById<ImageView>(R.id.photoPreview)
        btnSignUp = findViewById<Button>(R.id.btnSignUp)
        btnCancel = findViewById<ImageButton>(R.id.btnCancel)
        btnSelectDate = findViewById<ImageButton>(R.id.btnSelectDate_person)
        btnSelectPhoto =
            findViewById<ImageButton>(R.id.ivCamera) // Rename this button in your layout to be more generic
    }


    private fun setupClickListeners() {
        btnSignUp.setOnClickListener {
            registerPerson()
        }

        btnCancel.setOnClickListener {
            cleanScreen()
        }

        btnSelectPhoto.setOnClickListener {
            showPhotoSelectionDialog()
        }

        btnSelectDate.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun setupPhotoLaunchers() {
        // Initialize camera launcher
        cameraLauncher = Util.createCameraLauncher(
            activity = this,
            onPhotoCaptured = { bitmap ->
                selectedBitmap = bitmap
                userPhoto.setImageBitmap(bitmap)
                Util.showShortToast(this, "Photo captured successfully")
            },
            onCancel = {
                Util.showShortToast(this, "Photo capture cancelled")
            }
        )

        // Initialize gallery launcher
        galleryLauncher = Util.createGalleryLauncher(
            activity = this,
            onPhotoSelected = { bitmap ->
                selectedBitmap = bitmap
                userPhoto.setImageBitmap(bitmap)
                Util.showShortToast(this, "Photo selected successfully")
            },
            onError = { errorMessage ->
                Util.showShortToast(this, errorMessage)
            },
            onCancel = {
                Util.showShortToast(this, "Photo selection cancelled")
            }
        )
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

    private fun ResetDate() {
        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(this, this, year, month, day)
        datePickerDialog.show()
    }

    private fun getDateFormatString(dayOfMonth: Int, monthValue: Int, yearValue: Int): String {
        return "${if (dayOfMonth < 10) "0" else ""}$dayOfMonth/${if (monthValue < 10) "0" else ""}$monthValue/$yearValue"
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        this.year = year
        this.month = month
        this.day = dayOfMonth
        lbBirthdate.text = getDateFormatString(dayOfMonth, month + 1, year)
    }

    private fun isValidationData(): Boolean {
        if (lbBirthdate.text.isNullOrEmpty()) {
            Util.showShortToast(this, "Please select birth date")
            return false
        }

        val dateParse = Util.parseStringToDateModern(lbBirthdate.text.toString(), "dd/MM/yyyy")

        if (txtName.text.trim().isEmpty()) {
            Util.showShortToast(this, "Please enter name")
            return false
        }

        if (txtLastName.text.trim().isEmpty()) {
            Util.showShortToast(this, "Please enter last name")
            return false
        }

        if (txtEmail.text.trim().isEmpty()) {
            Util.showShortToast(this, "Please enter email")
            return false
        }

        if (txtPassword.text.trim().isEmpty() || txtPassword.text.length < 6) {
            Util.showShortToast(this, "Password must be at least 6 characters")
            return false
        }

        val phoneText = txtPhone.text.trim()
        if (phoneText.isEmpty() || phoneText.length < 8 || phoneText.toString()
                .toIntOrNull() == null
        ) {
            Util.showShortToast(this, "Please enter a valid phone number (at least 8 digits)")
            return false
        }

        if (dateParse == null) {
            Util.showShortToast(this, "Invalid date format")
            return false
        }

        return true
    }

    private fun cleanScreen() {
        ResetDate()
        txtName.setText("")
        txtLastName.setText("")
        txtEmail.setText("")
        txtPhone.setText("")
        txtPassword.setText("")
        lbBirthdate.text = ""
        userPhoto.setImageResource(android.R.color.transparent) // Clear photo
        selectedBitmap = null // Clear stored bitmap
    }

    private fun registerPerson() {
        lifecycleScope.launch {
            try {
                if (!isValidationData()) {
                    return@launch
                }

                val bDateParse =
                    Util.parseStringToDateModern(lbBirthdate.text.toString(), "dd/MM/yyyy")
                        ?: run {
                            Util.showShortToast(this@RegisterActivity, "Invalid date format")
                            return@launch
                        }

                // Store credentials for login attempt
                val email = txtEmail.text.toString().trim()
                val password = txtPassword.text.toString().trim()

                val person = Person().apply {
                    Name = txtName.text.toString().trim()
                    LastName = txtLastName.text.toString().trim()
                    Phone = txtPhone.text.toString().toIntOrNull() ?: 0
                    Email = email
                    Password = password
                    Birthday =
                        LocalDate.of(bDateParse.year, bDateParse.monthValue, bDateParse.dayOfMonth)
                    Photo = selectedBitmap
                }

                // Register the person
                val newId = personController.addPerson(person)
                Util.showShortToast(this@RegisterActivity, getString(R.string.MsgSaveSuccess))

                // Attempt to login with the same credentials
                val loginSuccess = personController.login(email, password)

                if (loginSuccess) {
                    Util.showShortToast(
                        this@RegisterActivity,
                        "Registration successful! Auto-login completed."
                    )
                    // Navigate to main activity or home screen
                    Util.openActivityAndFinish(
                        this@RegisterActivity,
                        HomeScreenActivity::class.java
                    )
                } else {
                    Util.showShortToast(
                        this@RegisterActivity,
                        "Registration successful! Please login manually."
                    )
                    cleanScreen()
                    // Optionally navigate to login screen
                    Util.openActivity(this@RegisterActivity, LoginActivity::class.java)
                }

            } catch (e: Exception) {
                Util.showShortToast(this@RegisterActivity, e.message ?: "Registration failed")
            }
        }
    }


}