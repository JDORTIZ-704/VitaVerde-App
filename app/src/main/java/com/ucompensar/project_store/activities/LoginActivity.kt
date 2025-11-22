@file:Suppress("DEPRECATION")

package com.ucompensar.project_store.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.material.textfield.TextInputEditText
import com.ucompensar.project_store.MainActivity
import com.ucompensar.project_store.R
import com.ucompensar.project_store.database.UsersDAO
import com.ucompensar.project_store.models.Users
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var input_mail_login: TextInputEditText
    private lateinit var input_password_login: TextInputEditText
    private val usersDAO by lazy { UsersDAO(this) }
    private val session by lazy { SessionManager(this) }

    private lateinit var credentialManagerGoogle: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "LoginActivity"
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Initialize DAO

        // Si ya hay sesión, salta a Main
        if (session.isLoggedIn()) {
            goToMain()
            return
        }

        initializeView()
        auth = FirebaseAuth.getInstance() //iniciar firebase

        val configuracionGoogle = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("495476343004-t8j802qetknfrkhbo2te1kcddfmal3gg.apps.googleusercontent.com")
            .requestEmail()
            .build()

        credentialManagerGoogle = GoogleSignIn.getClient(this, configuracionGoogle)



    }

    private fun initializeView() {

        // Inputs

        input_mail_login = findViewById(R.id.input_mail_login)
        input_password_login = findViewById(R.id.input_password_login)

        // Buttons

        val createUserButton: TextView = findViewById(R.id.btn_login_to_register)

        createUserButton.setOnClickListener {
            val intent = Intent(this, CreateUserActivity::class.java)
            startActivity(intent)
            finish()
        }

        val googleButton: Button = findViewById(R.id.btn_google_get_into_user)

        googleButton.setOnClickListener {
            loginWithGoogle()
            temporaryMessageEnterGoogle()
        }

        val loginButton: Button = findViewById(R.id.btn_create_user_login)

        loginButton.setOnClickListener {
            validateLogin()

        }

    }

    private fun validateLogin() {
        val mail = input_mail_login.text.toString().trim()
        val password = input_password_login.text.toString().trim()
        val user = usersDAO.getUserByEmail(mail)

        if (mail.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        when {
            mail.isEmpty() -> {
                input_mail_login.error = "Please enter a mail"
                input_mail_login.requestFocus()
                return
            }
            password.isEmpty() -> {
                input_password_login.error = "Please enter a password"
                input_password_login.requestFocus()
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches() -> {
                input_mail_login.error = "Please enter a valid mail"
                input_mail_login.requestFocus()
                return
            }
            else -> {

                // Cleaning error messages

                input_mail_login.error = null
                input_password_login.error = null

                // Logging into the app

                if (user != null && usersDAO.validateLogin(mail, password)) {
                    session.saveSession(user.id!!, user.email, "local")
                    temporaryMessageEnter()
                    goToMain()

                } else {
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loginWithGoogle() {
        val singInGoogleIntent = credentialManagerGoogle.signInIntent
        startActivityForResult(singInGoogleIntent, RC_SIGN_IN)

    }

    override fun onActivityResult (
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "Google sign in failed ${e.message}", Toast.LENGTH_SHORT)


            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, SetLocationActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun goToMain() {
        startActivity(Intent(this, SetLocationActivity::class.java))
        finish()
    }



    // Temporary messages

    private fun temporaryMessageEnterGoogle () {
        val toast = Toast.makeText(this, "Google Login", Toast.LENGTH_SHORT)
        toast.show()

        Handler(Looper.getMainLooper()).postDelayed({
            toast.cancel()
        },1000)
    }

    private fun temporaryMessageEnter () {
        val toast = Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT)
        toast.show()

        Handler(Looper.getMainLooper()).postDelayed({
            toast.cancel()
        },1000)
    }

}


