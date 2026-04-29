package com.campusdigitalfp.filmotecav2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    fun registerUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onResult(true, null)
                else onResult(false, translateErrorFirebase(task.exception))
            }
    }

    // Login
    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        // Si están vacíos, devolvemos el error antes de llamar a Firebase
        if (email.isBlank() || password.isBlank()) {
            onResult(false, "El correo y la contraseña no pueden estar vacíos.")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onResult(true, null)
                else onResult(false, translateErrorFirebase(task.exception))
            }
    }

    fun signInAnonymously(onResult: (Boolean, String?) -> Unit) {
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onResult(true, null)
                else onResult(false, translateErrorFirebase(task.exception))
            }
    }

    fun signInWithGoogle(idToken: String, onResult: (Boolean, String?) -> Unit) {
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onResult(true, null)
                else onResult(false, translateErrorFirebase(task.exception))
            }
    }

    fun logout() {
        auth.signOut()
    }

    private fun translateErrorFirebase(exception: Exception?): String {
        return when ((exception as? FirebaseAuthException)?.errorCode) {
            "ERROR_INVALID_EMAIL" -> "El correo electrónico no tiene un formato válido."
            "ERROR_WRONG_PASSWORD" -> "La contraseña es incorrecta."
            "ERROR_USER_NOT_FOUND" -> "No existe una cuenta con este correo."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "Este correo ya está registrado."
            "ERROR_WEAK_PASSWORD" -> "La contraseña es demasiado débil."
            "ERROR_NETWORK_REQUEST_FAILED" -> "Sin conexión a internet."
            else -> "Error: ${exception?.localizedMessage}"
        }
    }
}