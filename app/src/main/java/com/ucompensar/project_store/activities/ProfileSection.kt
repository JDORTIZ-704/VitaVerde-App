package com.ucompensar.project_store.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ucompensar.project_store.activities.WelcomeActivity
import com.ucompensar.project_store.databinding.FragmentProfileSectionBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.ucompensar.project_store.activities.SessionManager

class ProfileSection : Fragment() {

    private var _binding: FragmentProfileSectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileSectionBinding.inflate(inflater, container, false)

        sessionManager = SessionManager(requireContext())


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserData()

        binding.buttonLogout.setOnClickListener {
            handleUniversalLogout()
        }
    }

    /**
     * Carga los datos de usuario desde Google o desde el SessionManager.
     */
    private fun loadUserData() {
        val googleAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
        val isAppSession = sessionManager.isLoggedIn()

        if (googleAccount != null) {
            // CARGA GOOGLE
            binding.textViewUserName.text = googleAccount.displayName ?: "Usuario Google"
            binding.textViewUserEmail.text = googleAccount.email ?: "Email no disponible"
            // Color para Google
            binding.buttonLogout.setBackgroundColor(resources.getColor(android.R.color.holo_blue_light, null))
            binding.buttonLogout.text = "Cerrar Sesión"

        } else if (isAppSession) {
            val appUserName = sessionManager.getName() ?: "Usuario App"
            binding.textViewUserName.text = appUserName
            binding.textViewUserEmail.text = sessionManager.getEmail() ?: "Email App"
            // Cambiado el color a holo_green_dark (color principal de la App)
            binding.buttonLogout.setBackgroundColor(resources.getColor(android.R.color.holo_green_dark, null))
            // Texto simplificado
            binding.buttonLogout.text = "Cerrar Sesión"

        } else {
            binding.textViewUserName.text = "Invitado"
            binding.textViewUserEmail.text = "Inicia sesión para ver tu perfil"
            binding.buttonLogout.text = "Iniciar Sesión"
        }
    }

    /**
     * Determina el tipo de sesión y ejecuta el cierre de sesión apropiado.
     */
    private fun handleUniversalLogout() {
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        val isAppSession = sessionManager.isLoggedIn()

        if (account != null) {
            // Cierre de Google
            mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
                Toast.makeText(context, "Sesión de Google cerrada.", Toast.LENGTH_LONG).show()
                navigateToWelcomeActivity()
            }
        } else if (isAppSession) {

            handleAppLogout()
        } else {

            Toast.makeText(context, "Ya estás desconectado. Redirigiendo...", Toast.LENGTH_SHORT).show()
            navigateToWelcomeActivity()
        }
    }

    /**
     * Limpia la sesión local de la App.
     */
    private fun handleAppLogout() {
        sessionManager.clear()
        Toast.makeText(context, "Sesión normal de App cerrada.", Toast.LENGTH_LONG).show()
        navigateToWelcomeActivity()
    }

    /**
     * Navega a la WelcomeActivity y borra la pila de actividades.
     */
    private fun navigateToWelcomeActivity() {
        val intent = Intent(requireActivity(), WelcomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().finish() // Cierra la actividad que contiene el fragmento (MainActivity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}