import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.ucompensar.project_store.R
import kotlinx.coroutines.*
import java.util.Locale

class SetLocationActivity : AppCompatActivity() {

    private val fusedClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private lateinit var textView: TextView
    private lateinit var btn: Button

    private val requestFineLocation =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) fetchCityAndCountrySafe()
            else textView.text = "Permiso de ubicación denegado"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_set_location)

        textView = findViewById(R.id.textView5)
        btn = findViewById(R.id.btn_setlocation_confirmar)

        btn.setOnClickListener {
            ensurePermissionAndFetch()
        }
    }

    private fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    private fun ensurePermissionAndFetch() {
        if (!hasLocationPermission()) {
            requestFineLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            fetchCityAndCountrySafe()
        }
    }

    /**
     * This method is ONLY called if we have already verified the permission.
     * Even so, we re-verify for security and catch a SecurityException.
     */
    @SuppressLint("MissingPermission")
    private fun fetchCityAndCountrySafe() {
        if (!hasLocationPermission()) return  // Extra validation

        try {
            fusedClient.lastLocation
                .addOnSuccessListener { loc: Location? ->
                    if (loc != null) reverseGeocode(loc)
                    else textView.text = "Ubicación nula, intenta nuevamente"
                }
                .addOnFailureListener {
                    textView.text = "Error al obtener ubicación"
                }
        } catch (se: SecurityException) {
            textView.text = "Sin permiso de ubicación"
        }
    }

    private fun reverseGeocode(location: Location) {
        uiScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val geocoder = Geocoder(this@SetLocationActivity, Locale.getDefault())
                    val list = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    val addr = list?.firstOrNull()
                    val city = addr?.locality ?: addr?.subAdminArea ?: addr?.adminArea
                    val country = addr?.countryName ?: addr?.countryCode
                    if (!city.isNullOrBlank() && !country.isNullOrBlank()) "$city, $country" else null
                } catch (_: Exception) { null }
            }
            textView.text = result ?: "No se pudo resolver ciudad/país"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }
}
