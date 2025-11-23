package com.ucompensar.project_store.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.ucompensar.project_store.R
import com.ucompensar.project_store.database.UsersDAO
import com.ucompensar.project_store.models.Users
import kotlinx.coroutines.*
import java.util.Locale


class AdminCreateUserActivity : AppCompatActivity() {


    private val fusedClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())


    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var txtCity: TextView
    private lateinit var etRole: EditText
    private lateinit var btnCreateUser: Button


    private val requestFineLocation =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                fetchCityAndCountrySafe()
            } else {
                txtCity.text = "Permiso denegado. Ciudad requerida."
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_create_user)


        txtCity = findViewById(R.id.txt_admin_register_city)
        etName = findViewById(R.id.input_admin_register_username)
        etRole = findViewById(R.id.input_admin_register_role)
        etEmail = findViewById(R.id.input_admin_register_email)
        etPassword = findViewById(R.id.input_admin_register_password)
        btnCreateUser = findViewById(R.id.btn_admin_create_user_register)


        ensurePermissionAndFetch()

        btnCreateUser.setOnClickListener {
            handleAdminRegistration()
        }
    }

    private fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    private fun ensurePermissionAndFetch() {
        if (!hasLocationPermission()) {
            txtCity.text = "Obteniendo ubicación..."
            requestFineLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            fetchCityAndCountrySafe()
        }
    }

    /**
     * Este método se llama si ya verificamos el permiso o si fue concedido.
     */
    @SuppressLint("MissingPermission")
    private fun fetchCityAndCountrySafe() {
        if (!hasLocationPermission()) {
            txtCity.text = "Permiso de ubicación denegado"
            return
        }

        txtCity.text = "Cargando ubicación..."
        try {
            fusedClient.lastLocation
                .addOnSuccessListener { loc: Location? ->
                    if (loc != null) reverseGeocode(loc)
                    else txtCity.text = "Ubicación nula, revisa tu GPS"
                }
                .addOnFailureListener {
                    txtCity.text = "Error al obtener ubicación"
                }
        } catch (se: SecurityException) {
            txtCity.text = "Sin permiso de ubicación"
        }
    }

    private fun reverseGeocode(location: Location) {
        uiScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val geocoder = Geocoder(this@AdminCreateUserActivity, Locale.getDefault())
                    val list = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    val addr = list?.firstOrNull()
                    val city = addr?.locality ?: addr?.subAdminArea ?: addr?.adminArea
                    val country = addr?.countryName
                    if (!city.isNullOrBlank() && !country.isNullOrBlank()) {
                        city
                    }
                    else null
                } catch (_: Exception) {
                    null
                }
            }
            txtCity.text = result ?: "Ubicación desconocida, toca para reintentar"
            if (result == null) {
                txtCity.setOnClickListener { ensurePermissionAndFetch() }
            } else {
                txtCity.setOnClickListener(null) // Desactivar si la ubicación es exitosa
            }
        }
    }

    private fun handleAdminRegistration() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val city = txtCity.text.toString().trim()
        val role = etRole.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || city.isEmpty() || role.isEmpty() || city.contains("Permiso denegado") || city.contains("Cargando ubicación") || city.contains("Ubicación nula") || city.contains("Error al obtener ubicación")) {
            Toast.makeText(this, "Por favor, completa todos los campos y/o verifica la ubicación.", Toast.LENGTH_LONG).show()
            return
        }

        val usersDAO = UsersDAO(this)


        if (usersDAO.validateEmail(email)) {
            Toast.makeText(this, "Error: El correo '$email' ya está registrado.", Toast.LENGTH_LONG).show()
            return
        }

        val newUser = Users(
            name = name,
            email = email,
            password = password,
            isAdmin = true,
            city = city,
            role = role
        )

        if (usersDAO.registerUser(newUser)) {
            Toast.makeText(this, "Administrador '$name' creado exitosamente!", Toast.LENGTH_LONG).show()

            startActivity(Intent(this, AdminDashboardActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Error: Falló la inserción del nuevo administrador.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel() // Cancelar coroutines pendientes al destruir la actividad
    }
}