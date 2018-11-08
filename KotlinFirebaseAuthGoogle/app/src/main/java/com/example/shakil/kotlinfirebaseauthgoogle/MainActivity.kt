package com.example.shakil.kotlinfirebaseauthgoogle

import android.app.AlertDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    override fun onConnectionFailed(p0: ConnectionResult) {
        Toast.makeText(this, "" + p0.errorMessage, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private val PERMISSION_SIGN_IN = 9999
    }

    lateinit var mGoogleApiClient: GoogleApiClient
    lateinit var firebaseAuth: FirebaseAuth

    lateinit var waiting_dialog: AlertDialog

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSION_SIGN_IN) {

            waiting_dialog.show()

            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {

                waiting_dialog.dismiss()

                val account = result.signInAccount
                val idToken = account!!.idToken

                val credential = GoogleAuthProvider.getCredential(idToken, null)
                firebaseAuthWithGoogle(credential)
            } else {
                waiting_dialog.dismiss()

                Log.e("ERROR", "Login failed")
                Log.e("ERROR", result.status.statusMessage)
            }
        }
    }

    private fun firebaseAuthWithGoogle(credential: AuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                //Start new Activity and pass email to new Activity
                val intent = Intent(this@MainActivity, LoggedActivity::class.java)
                intent.putExtra("email", authResult.user.email)
                startActivity(intent)
            }.addOnFailureListener {
                    e -> Toast.makeText(this@MainActivity, "" + e.message, Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureGoogleSignIn()

        firebaseAuth = FirebaseAuth.getInstance()

        waiting_dialog = SpotsDialog.Builder().setContext(this)
            .setMessage("Please wait...")
            .setCancelable(false).build()

        btn_sign_in.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(intent, PERMISSION_SIGN_IN)
    }

    private fun configureGoogleSignIn() {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, options).build()

        mGoogleApiClient.connect()
    }

}
