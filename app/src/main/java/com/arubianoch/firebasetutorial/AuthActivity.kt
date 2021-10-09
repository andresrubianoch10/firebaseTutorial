package com.arubianoch.firebasetutorial

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.arubianoch.firebasetutorial.databinding.ActivityAuthBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    private val analytics by lazy { FirebaseAnalytics.getInstance(this) }

    private val auth by lazy(Firebase::auth)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        handleUserLogged()
    }

    private fun handleUserLogged() {
        sendAnalytics()
        setTitle()
        val currentUser = auth.currentUser
        currentUser?.let {
            showHome(it.email.orEmpty(), ProviderType.BASIC)
        } ?: setUp()
    }

    private fun sendAnalytics() {
        val bundle = Bundle()
        bundle.putString("message", "Integración firebase completa")
        analytics.logEvent("mainActivity", bundle)
    }

    private fun setTitle() {
        title = "Autenticación"
    }

    private fun setUp() {
        signUpClick()
        signInClick()
    }

    private fun signUpClick() {
        with(binding) {
            buttonAuthSignUp.setOnClickListener {
                if (fieldsAreCorrectlyCompleted()) {
                    auth.createUserWithEmailAndPassword(getEmail(), getPassword())
                        .addOnCompleteListener(this@AuthActivity::handleAuthResult)
                }
            }
        }
    }

    private fun signInClick() {
        with(binding) {
            buttonAuthSignIn.setOnClickListener {
                if (fieldsAreCorrectlyCompleted()) {
                    auth.signInWithEmailAndPassword(getEmail(), getPassword())
                        .addOnCompleteListener(this@AuthActivity::handleAuthResult)
                }
            }
        }
    }

    private fun handleAuthResult(data: Task<AuthResult>) {
        if (data.isSuccessful) {
            showHome(data.result?.user?.email.orEmpty(), ProviderType.BASIC)
        } else {
            showAlert()
        }
    }

    private fun fieldsAreCorrectlyCompleted(): Boolean {
        if (getEmail().isNotEmpty() &&
            getPassword().isNotEmpty()
        ) {
            return true
        }
        return false
    }

    private fun getEmail() = binding.editTextAuthEmail.text.toString()

    private fun getPassword() = binding.editTextAuthPassword.text.toString()

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
}