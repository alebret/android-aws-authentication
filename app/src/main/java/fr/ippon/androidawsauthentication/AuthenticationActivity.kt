package fr.ippon.androidawsauthentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserState
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.client.results.SignInResult
import com.amazonaws.mobile.client.results.SignInState
import kotlinx.android.synthetic.main.activity_authentication.*


class AuthenticationActivity : AppCompatActivity() {

    private val TAG = AuthenticationActivity::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        AWSMobileClient.getInstance()
            .initialize(applicationContext, object : Callback<UserStateDetails> {
                override fun onResult(userStateDetails: UserStateDetails) {
                    Log.i(TAG, userStateDetails.userState.toString())
                    when (userStateDetails.userState) {
                        UserState.SIGNED_IN -> goToMainActivity()
                        else -> AWSMobileClient.getInstance().signOut()
                    }
                }

                override fun onError(e: Exception) {
                    Log.e(TAG, e.toString())
                }
            })

        buttonLogin.setOnClickListener {
            signIn(editTextLogin.text.toString(), editTextPwd.text.toString())
        }

    }

    private fun signIn(login: String, password: String) {
        AWSMobileClient.getInstance()
            .signIn(login, password, null, object : Callback<SignInResult> {
                override fun onResult(signInResult: SignInResult) {
                    Log.d(
                        TAG,
                        "Sign-in callback state: " + signInResult.signInState
                    )
                    when (signInResult.signInState) {
                        SignInState.DONE -> {
                            Log.i(TAG, "Sign-in done.")
                            goToMainActivity()
                        }
                        SignInState.SMS_MFA -> Log.i(TAG, "Please confirm sign-in with SMS.")
                        SignInState.NEW_PASSWORD_REQUIRED -> Log.i(
                            TAG,
                            "Please confirm sign-in with new password."
                        )
                        else -> Log.i(
                            TAG,
                            "Unsupported sign-in confirmation: " + signInResult.signInState
                        )
                    }
                }

                override fun onError(e: Exception) {
                    Log.e(TAG, e.toString())
                }
            })
    }

    fun goToMainActivity() {
        val i = Intent(this@AuthenticationActivity, MainActivity::class.java)
        startActivity(i)
    }

}
