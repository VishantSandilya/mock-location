    package `in`.tutorials.mocklocation

    import android.app.Notification
    import android.app.NotificationChannel
    import android.app.NotificationManager
    import android.app.Service
    import android.content.Intent
    import android.location.Location
    import android.location.LocationManager
    import android.location.provider.ProviderProperties
    import android.os.Build
    import android.os.IBinder
    import android.os.SystemClock
    import androidx.core.app.NotificationCompat
    import java.util.*

    class LocationMockService : Service() {

        private lateinit var locationManager: LocationManager
        private var mockLatitude: Double = 0.0
        private var mockLongitude: Double = 0.0
        private var isMocking = false

        companion object {
            private const val NOTIFICATION_ID = 1001
            private const val CHANNEL_ID = "LocationMockChannel"
            private const val UPDATE_INTERVAL = 1000L // 1 second
        }

        override fun onCreate() {
            super.onCreate()
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            createNotificationChannel()
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            intent?.let {
                mockLatitude = it.getDoubleExtra("latitude", 0.0)
                mockLongitude = it.getDoubleExtra("longitude", 0.0)
            }

            startForeground(NOTIFICATION_ID, createNotification())
            startLocationMocking()

            return START_STICKY
        }

        override fun onBind(intent: Intent?): IBinder? = null

        private fun createNotificationChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Location Mock Service",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Shows when location mocking is active"
                }

                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
            }
        }

        private fun createNotification(): Notification {
            return NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Mocking Active")
                .setContentText("Mocking location: $mockLatitude, $mockLongitude")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build()
        }

        private fun startLocationMocking() {
            if (isMocking) return

            isMocking = true

            // Add mock location provider
            try {
                locationManager.addTestProvider(
                    LocationManager.GPS_PROVIDER,
                    false, false, false, false, false,
                    true, true, 1, ProviderProperties.ACCURACY_FINE // POWER_LOW, ACCURACY_FINE
                )
                locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)
            } catch (e: SecurityException) {
                // Handle case where mock location is not allowed
                stopSelf()
                return
            }

            // Start periodic location updates
            Timer().scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (isMocking) {
                        provideMockLocation()
                    }
                }
            }, 0, UPDATE_INTERVAL)
        }

        private fun provideMockLocation() {
            try {
                val mockLocation = Location(LocationManager.GPS_PROVIDER).apply {
                    latitude = mockLatitude
                    longitude = mockLongitude
                    altitude = 0.0
                    time = System.currentTimeMillis()
                    setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos())
                    accuracy = 1.0f
    //                isFromMockProvider = true
                }

                locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, mockLocation)
            } catch (e: Exception) {
                // Handle any exceptions during location setting
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            stopLocationMocking()
        }

        private fun stopLocationMocking() {
            isMocking = false

            try {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.removeTestProvider(LocationManager.GPS_PROVIDER)
                }
            } catch (e: Exception) {
                // Handle exceptions when removing test provider
            }
        }
    }