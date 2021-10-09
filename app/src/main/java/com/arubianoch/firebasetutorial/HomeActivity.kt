package com.arubianoch.firebasetutorial

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.arubianoch.firebasetutorial.databinding.ActivityHomeBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

enum class ProviderType {
    BASIC
}

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private val auth by lazy(Firebase::auth)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        setUp()
    }

    private fun setUp() {
        setTitle()
        setInfo()
        setSignOutListener()
    }

    private fun setTitle() {
        title = "Inicio"
    }

    private fun setInfo() {
        val bundle = intent.extras
        bundle?.let {
            val email = it.getString("email")
            val type = it.getString("provider")
            binding.textViewHomeUserName.text = email
            binding.textViewHomeUserType.text = type
        } ?: goBack()
    }

    private fun setSignOutListener() {
        binding.buttonHomeCloseSession.setOnClickListener {
            auth.signOut()
            goBack()
        }
    }

    private fun goBack() {
        onBackPressed()
    }
}