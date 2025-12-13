package com.ACID.geojournal

import Controller.PersonController
import Util.Util
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class HomeScreenActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 0
    }
    private lateinit var Addbtn: Button
    private lateinit var Historybtn: Button
    private lateinit var Userbtn: Button
    private lateinit var Logoutbtn: Button
    private lateinit var root: ViewGroup
    private lateinit var menuBtn: Button
    private lateinit var drop: LinearLayout
    private lateinit var personController: PersonController
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        personController = PersonController(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        initializeViews()
        setupClickListeners()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }
    fun initializeViews() {
        Addbtn = findViewById<Button>(R.id.btnAddHistory)
        Historybtn = findViewById<Button>(R.id.Historybtn)
        Userbtn = findViewById<Button>(R.id.Userbtn)
        Logoutbtn = findViewById<Button>(R.id.logoutBtn)
        root = findViewById(R.id.main)
        menuBtn = findViewById(R.id.mnu)
        drop = findViewById(R.id.dropContainer)
    }
    fun setupClickListeners() {
        Addbtn.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val currentUser = personController.getCurrentUser()

                    if (currentUser?.ID != null) {
                        Util.personID = currentUser.ID
                        Util.openActivity(
                            this@HomeScreenActivity,
                            HistoryActivity::class.java
                        )
                    } else {
                        showToast("Error: User not identified")
                    }
                } catch (e: Exception) {
                    showToast("Error: User not identified")
                }
            }
        }
        Historybtn.setOnClickListener {
            Util.openActivity(this, HistoryListActivity::class.java)
        }
        Userbtn.setOnClickListener {
            Util.openActivity(this, UserActivity::class.java)
        }
        Logoutbtn.setOnClickListener {
            showLogoutConfirmationDialog()
        }
        menuBtn.setOnClickListener {
            val shouldShow = drop.visibility == View.GONE

            // Animate layout changes (ConstraintLayout will reflow nicely)
            TransitionManager.beginDelayedTransition(root)

            if (shouldShow) {
                drop.visibility = View.VISIBLE

                // staggered fade/slide in
                val lastIndex = drop.childCount - 1
                for (i in 0..lastIndex) {
                    val child = drop.getChildAt(i)
                    child.alpha = 0f
                    child.translationY = -20f
                    child.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setStartDelay((i * 40).toLong())
                        .setDuration(160)
                        .start()
                }
                // optional: update contentDescription for accessibility
                menuBtn.contentDescription = "Close menu"
            } else {
                // staggered fade/slide out
                val lastIndex = drop.childCount - 1
                for (i in 0..lastIndex) {
                    val child = drop.getChildAt(i)
                    child.animate()
                        .alpha(0f)
                        .translationY(-20f)
                        .setStartDelay((i * 30).toLong())
                        .setDuration(140)
                        .withEndAction {
                            // when last child finished animating, hide container
                            if (i == lastIndex) {
                                drop.visibility = View.GONE
                            }
                        }
                        .start()
                }
                menuBtn.contentDescription = "Open menu"
            }
        }
    }
    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton(R.string.TextYes) { dialog, which ->
                logoutUser()
            }
            .setNegativeButton(R.string.TextNo, null)
            .show()
    }
    private fun logoutUser() {
        lifecycleScope.launch {
            try {
                val success = personController.logout()

                if (success) {
                    showToast("Logged out successfully")
                    // Navigate to login screen
                    Util.openActivityAndFinish(this@HomeScreenActivity, LoginActivity::class.java)
                } else {
                    showToast("Logout failed. Please try again.")
                }
            } catch (e: Exception) {
                showToast("Error during logout: ${e.message}")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        locationEnable()
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    val myLocation = LatLng(it.latitude, it.longitude)

                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            myLocation,
                            16f // zoom recomendado
                        )
                    )
                }
            }
    }
    private fun locationAllowed()=ContextCompat.
    checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun locationEnable() {
        if (!::map.isInitialized) return
        if (locationAllowed()) {
            map.isMyLocationEnabled = true
        } else {
            requestLocation()
        }
    }
    private fun requestLocation() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Util.showShortToast(this, getString(R.string.permits))
        }else {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
            }else{
                Util.showShortToast(this, getString(R.string.permits))
            }
            else ->{}
        }
    }

}
