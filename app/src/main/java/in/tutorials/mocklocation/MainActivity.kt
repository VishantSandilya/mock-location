package `in`.tutorials.mocklocation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity() {

    private lateinit var latitudeInput: TextInputEditText
    private lateinit var longitudeInput: TextInputEditText
    private lateinit var startMockingButton: MaterialButton
    private lateinit var stopMockingButton: MaterialButton
    private lateinit var statusText: MaterialTextView
    private lateinit var currentLocationCard: MaterialCardView

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            checkMockLocationEnabled()
        } else {
            Toast.makeText(this, "Location permissions are required", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupClickListeners()
        checkPermissions()
    }

    private fun initializeViews() {
        latitudeInput = findViewById(R.id.latitudeInput)
        longitudeInput = findViewById(R.id.longitudeInput)
        startMockingButton = findViewById(R.id.startMockingButton)
        stopMockingButton = findViewById(R.id.stopMockingButton)
        statusText = findViewById(R.id.statusText)
        currentLocationCard = findViewById(R.id.currentLocationCard)

        // Set default coordinates (New York City)
        latitudeInput.setText("28.4991707")
        longitudeInput.setText("77.0697687")
    }

    private fun setupClickListeners() {
        startMockingButton.setOnClickListener {
            if (validateInputs()) {
                startLocationMocking()
            }
        }

        stopMockingButton.setOnClickListener {
            stopLocationMocking()
        }
    }

    private fun validateInputs(): Boolean {
        val latitude = latitudeInput.text.toString().toDoubleOrNull()
        val longitude = longitudeInput.text.toString().toDoubleOrNull()

        if (latitude == null || longitude == null) {
            Toast.makeText(this, "Please enter valid coordinates", Toast.LENGTH_SHORT).show()
            return false
        }

        if (latitude < -90 || latitude > 90) {
            Toast.makeText(this, "Latitude must be between -90 and 90", Toast.LENGTH_SHORT).show()
            return false
        }

        if (longitude < -180 || longitude > 180) {
            Toast.makeText(this, "Longitude must be between -180 and 180", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            locationPermissionLauncher.launch(permissionsToRequest)
        } else {
            checkMockLocationEnabled()
        }
    }

    private fun checkMockLocationEnabled() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showEnableGpsDialog()
        }
    }

    private fun showEnableGpsDialog() {
        AlertDialog.Builder(this)
            .setTitle("GPS Required")
            .setMessage("GPS must be enabled for location mocking to work. Would you like to enable it?")
            .setPositiveButton("Enable GPS") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun startLocationMocking() {
        val latitude = latitudeInput.text.toString().toDouble()
        val longitude = longitudeInput.text.toString().toDouble()

        val intent = Intent(this, LocationMockService::class.java).apply {
            putExtra("latitude", latitude)
            putExtra("longitude", longitude)
        }

        startForegroundService(intent)
        updateUI(true)
        Toast.makeText(this, "Location mocking started", Toast.LENGTH_SHORT).show()
    }

    private fun stopLocationMocking() {
        val intent = Intent(this, LocationMockService::class.java)
        stopService(intent)
        updateUI(false)
        Toast.makeText(this, "Location mocking stopped", Toast.LENGTH_SHORT).show()
    }

    private fun updateUI(isMocking: Boolean) {
        if (isMocking) {
            startMockingButton.isEnabled = false
            stopMockingButton.isEnabled = true
            statusText.text = "Status: Mocking Location"
            currentLocationCard.visibility = android.view.View.VISIBLE
        } else {
            startMockingButton.isEnabled = true
            stopMockingButton.isEnabled = false
            statusText.text = "Status: Stopped"
            currentLocationCard.visibility = android.view.View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        // Check if service is running and update UI accordingly
        // This is a simplified check - in a real app you'd use a more robust method
    }
} 