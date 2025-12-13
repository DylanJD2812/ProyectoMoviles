package com.ACID.geojournal

import Controller.PersonController
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var tvForgotPassword: TextView
    private lateinit var btnLogin: Button
    private lateinit var personController: PersonController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        personController = PersonController(this)
        initializeViews()
        setupClickListeners()
    }
    private fun initializeViews() {
        txtEmail = findViewById(R.id.emailText_login)
        txtPassword = findViewById(R.id.passText_login)
        btnLogin = findViewById(R.id.btnLogin_login)
        tvForgotPassword = findViewById(R.id.tvForgotPassword_login)
    }

    private fun setupClickListeners() {
        btnLogin.setOnClickListener { login() }
        tvForgotPassword.setOnClickListener { sendResetPassword() }
    }

    private fun login() {
        val email = txtEmail.text.toString().trim()
        val password = txtPassword.text.toString()

        if (email.isEmpty()) {
            Util.Util.showShortToast(this, "Please enter your email")
            return
        }

        if (password.isEmpty()) {
            Util.Util.showShortToast(this, "Please enter your password")
            return
        }

        lifecycleScope.launch {
            val success = personController.login(email, password)
            if (success) {
                Util.Util.showShortToast(this@LoginActivity, "Login successful")
                Util.Util.openActivityAndFinish(
                    this@LoginActivity,
                    HomeScreenActivity::class.java
                )
            } else {
                Util.Util.showShortToast(this@LoginActivity, "Invalid email or password")
            }
        }
    }

    private fun sendResetPassword() {
        val email = txtEmail.text.toString().trim()
        if (email.isEmpty()) {
            Util.Util.showShortToast(this, "Please enter your email to reset password")
            return
        }

        lifecycleScope.launch {
            val success = personController.resetPassword(email)
            if (success) {
                Util.Util.showShortToast(this@LoginActivity, "Password reset email sent")
            } else {
                Util.Util.showShortToast(this@LoginActivity, "Failed to send reset email")
            }
        }
    }
}