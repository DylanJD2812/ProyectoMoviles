package com.ACID.geojournal

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.ImageView
import com.ACID.geojournal.R.*

class MainActivity : AppCompatActivity() {
    private lateinit var bg: ImageView
    private var kenBurnsSet: AnimatorSet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        bg = findViewById(R.id.parallaxBg)

        bg.post {
            startKenBurnsFromBottom()
        }

        val openSignUp = findViewById<Button>(R.id.OpenActivitybtn)
        openSignUp.setOnClickListener(View.OnClickListener { view ->
            Util.Util.openActivity(this, RegisterActivity::class.java)
        })
        val openSignIn = findViewById<Button>(R.id.btnSignIn_main)
        openSignIn.setOnClickListener(View.OnClickListener { view ->
            Util.Util.openActivity(this, LoginActivity::class.java)
        })
    }


    private fun startKenBurnsFromBottom() {
        kenBurnsSet?.cancel()

        val startScale = 1.25f      // empieza “zoomeada”
        val endScale = 1.10f        // termina un poquito menos zoom (puedes poner 1.25f si quieres mantener zoom)
        val maxPan = 120f           // cuánto se mueve vertical (ajusta 60f–180f)
        val durationMs = 14000L     // 10–18s se siente premium

        // Estado inicial: zoom + “abajo”
        bg.scaleX = startScale
        bg.scaleY = startScale
        bg.translationY = maxPan

        val panUp = ObjectAnimator.ofFloat(bg, "translationY", maxPan, -maxPan).apply {
            duration = durationMs
            interpolator = LinearInterpolator()
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
        }

        val zoom = ObjectAnimator.ofFloat(bg, "scaleX", startScale, endScale).apply {
            duration = durationMs
            interpolator = LinearInterpolator()
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
        }
        val zoomY = ObjectAnimator.ofFloat(bg, "scaleY", startScale, endScale).apply {
            duration = durationMs
            interpolator = LinearInterpolator()
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
        }

        kenBurnsSet = AnimatorSet().apply {
            playTogether(panUp, zoom, zoomY)
            start()
        }
    }

    override fun onPause() {
        super.onPause()
        kenBurnsSet?.pause()
    }

    override fun onResume() {
        super.onResume()
        kenBurnsSet?.resume()
    }
}

