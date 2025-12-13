package com.ACID.geojournal

import Controller.PersonController
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import coil.load


class UserActivity : AppCompatActivity() {
    private lateinit var btnSignOut: Button
    private lateinit var personController: PersonController
    private lateinit var name: TextView
    private lateinit var lastname: TextView
    private lateinit var phone: TextView
    private lateinit var email: TextView
    private lateinit var birthdate: TextView
    private lateinit var userId: TextView
    private lateinit var profilePhoto: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        personController = PersonController(this)
        initializeView()
        setupClickListeners()
        loadUserData()
    }
    private fun initializeView() {
        btnSignOut = findViewById(R.id.btnSignOut_User)
        name = findViewById(R.id.tvUserName)
        lastname = findViewById(R.id.tvUserLastname)
        phone = findViewById(R.id.tvUserPhone)
        email = findViewById(R.id.tvUserEmail)
        birthdate = findViewById(R.id.tvUserBirthdate)
        userId = findViewById(R.id.tvUserId)
        profilePhoto = findViewById(R.id.imgProfilePhoto)
    }
    private fun setupClickListeners() {
        btnSignOut.setOnClickListener {
            logoutUser()
        }
    }
    private fun loadUserData() {
        lifecycleScope.launch {
            try{
                val user = personController.getCurrentUser()

                name.text = user?.Name
                lastname.text = user?.LastName
                phone.text = user?.Phone.toString()
                email.text = user?.Email
                birthdate.text = user?.Birthday.toString()
                userId.text = user?.ID
                profilePhoto.load(user?.PhotoUrl) {
                    placeholder(R.drawable.ic_launcher_foreground)
                    error(R.drawable.ic_launcher_foreground)
                    crossfade(true)
                }

            }catch (e: Exception){

            }
        }
    }
    private fun logoutUser() {
        lifecycleScope.launch {
            try {
                val success = personController.logout()
                if (success) {
                    Util.Util.personID = null
                    Util.Util.openActivityAndFinish(this@UserActivity, MainActivity::class.java)
                } else{
                    Util.Util.showShortToast(this@UserActivity, "Error")
                }
            }catch (e: Exception){
                Util.Util.showShortToast(this@UserActivity, "Error: ${e.message}")
            }
        }
    }
}