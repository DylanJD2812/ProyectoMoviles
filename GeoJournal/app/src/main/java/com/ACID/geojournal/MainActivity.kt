package com.ACID.geojournal

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import com.ACID.geojournal.R.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val openSignUp = findViewById<Button>(R.id.OpenActivitybtn)
        openSignUp.setOnClickListener(View.OnClickListener{ view ->
            Util.Util.openActivity(this, RegisterActivity::class.java)
        })
        val openSignIn = findViewById<Button>(R.id.btnSignIn_main)
        openSignIn.setOnClickListener(View.OnClickListener{view ->
            Util.Util.openActivity(this, LoginActivity::class.java)
        })

    }
}

