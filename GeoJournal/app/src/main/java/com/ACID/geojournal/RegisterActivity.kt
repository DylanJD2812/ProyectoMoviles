package com.ACID.geojournal

import com.ACID.geojournal.R
import Controller.PersonController
import Entity.Person
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
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
    private lateinit var btnCancel: Button

    private var day: Int = 0
    private var month: Int = 0
    private var year: Int = 0

    private lateinit var personController: PersonController

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
        btnSignUp = findViewById<Button>(R.id.btnSignUp)
        btnCancel = findViewById<Button>(R.id.btnCancel)
    }

    private fun setupClickListeners() {
        val btnSelectDate = findViewById<ImageButton>(R.id.btnSelectDate_person)
        btnSelectDate.setOnClickListener {
            showDatePickerDialog()
        }

        btnSignUp.setOnClickListener {
            registerPerson()
        }

        btnCancel.setOnClickListener {
            cleanScreen()
        }
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
            showToast("Please select birth date")
            return false
        }

        val dateParse = Util.Util.parseStringToDateModern(lbBirthdate.text.toString(), "dd/MM/yyyy")

        // Validate all required fields
        if (txtName.text.trim().isEmpty()) {
            showToast("Please enter name")
            return false
        }

        if (txtLastName.text.trim().isEmpty()) {
            showToast("Please enter last name")
            return false
        }

        if (txtEmail.text.trim().isEmpty()) {
            showToast("Please enter email")
            return false
        }

        if (txtPassword.text.trim().isEmpty() || txtPassword.text.length < 6) {
            showToast("Password must be at least 6 characters")
            return false
        }

        val phoneText = txtPhone.text.trim()
        if (phoneText.isEmpty() || phoneText.length < 8 || phoneText.toString().toIntOrNull() == null) {
            showToast("Please enter a valid phone number (at least 8 digits)")
            return false
        }

        if (dateParse == null) {
            showToast("Invalid date format")
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
    }

    private fun registerPerson() {
        lifecycleScope.launch {
            try {
                if (!isValidationData()) {
                    return@launch
                }

                val bDateParse = Util.Util.parseStringToDateModern(lbBirthdate.text.toString(), "dd/MM/yyyy")
                    ?: run {
                        showToast("Invalid date format")
                        return@launch
                    }

                val person = Person().apply {
                    // ID will be generated by Firebase
                    Name = txtName.text.toString().trim()
                    LastName = txtLastName.text.toString().trim()
                    Phone = txtPhone.text.toString().toIntOrNull() ?: 0
                    Email = txtEmail.text.toString().trim()
                    Password = txtPassword.text.toString().trim()
                    Birthday = LocalDate.of(bDateParse.year, bDateParse.monthValue, bDateParse.dayOfMonth)
                    Photo = null
                }

                val newId = personController.addPerson(person)
                showToast(getString(R.string.MsgSaveSuccess))
                cleanScreen()

                // Optionally navigate to another activity after successful registration
                // finish()

            } catch (e: Exception) {
                showToast(e.message ?: "Registration failed")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}