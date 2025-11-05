package com.ACID.geojournal

import Controller.PersonController
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class RegisterActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private lateinit var txtId: EditText
    private lateinit var txtName: EditText
    private lateinit var txtFLastName: EditText
    private lateinit var txtSLastName: EditText
    private lateinit var txtPhone: EditText
    private lateinit var txtEmail: EditText
    private lateinit var lbBirthdate: TextView

    private  var day :Int=0
    private  var month: Int=0
    private  var year: Int =0

    private var isEditMode: Boolean = false
    private lateinit var personController: PersonController

    private lateinit var menuItemDelete: MenuItem

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
        txtName=findViewById<EditText>(R.id.nameText)
        txtFLastName=findViewById<EditText>(R.id.fnameText)
        txtSLastName=findViewById<EditText>(R.id.snameText)
        txtPhone=findViewById<EditText>(R.id.phoneText)
        txtEmail=findViewById<EditText>(R.id.emailText)
        lbBirthdate=findViewById<TextView>(R.id.lbBirthdate_Person)

        ResetDate ()

        val btnSelectDate = findViewById<ImageButton>(R.id.btnSelectDate_person)
        btnSelectDate.setOnClickListener(View.OnClickListener{ view ->
            showDatePickerDialog()
        })
        
        val btnSearch = findViewById<ImageButton>(R.id.btnSearchId_person)
        btnSearch.setOnClickListener(View.OnClickListener{view ->
            searchPerson(txtId.text.trim().toString())
        })

    }

    private fun ResetDate (){
        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)

    }
}