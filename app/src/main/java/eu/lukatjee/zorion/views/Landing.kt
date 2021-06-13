package eu.lukatjee.zorion.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import eu.lukatjee.zorion.R
import eu.lukatjee.zorion.authentication.Signin
import eu.lukatjee.zorion.authentication.Signup
import java.lang.NullPointerException

class Landing : AppCompatActivity(), View.OnClickListener {

    private lateinit var signInButton : Button
    private lateinit var signUpButton : Button

    private lateinit var intentExtras : Bundle

    override fun onCreate(savedInstanceState: Bundle?) {

        FirebaseApp.initializeApp(/*context=*/ this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(SafetyNetAppCheckProviderFactory.getInstance())

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        intent.putExtra("FROM_ACTIVITY", "NONE")
        intentExtras = intent.extras!!

        signInButton = findViewById(R.id.landingSignInButton)
        signUpButton = findViewById(R.id.landingSignUpButton)

        signInButton.setOnClickListener(this)
        signUpButton.setOnClickListener(this)

    }

    override fun onBackPressed() {

        super.onBackPressed()

        when (intentExtras.getString("FROM_ACTIVITY")) {

            else -> finishAffinity()

        }

    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.landingSignInButton -> {

                val authenticationSignIn = Intent(this, Signin::class.java)

                authenticationSignIn.putExtra("FROM_ACTIVITY", "LANDING")
                authenticationSignIn.putExtra("TO_ACTIVITY", "SIGN_IN")

                startActivity(authenticationSignIn)

            }

            R.id.landingSignUpButton -> {

                val authenticationSignUp = Intent(this, Signup::class.java)

                authenticationSignUp.putExtra("FROM_ACTIVITY", "LANDING")
                authenticationSignUp.putExtra("TO_ACTIVITY", "SIGN_UP")

                startActivity(authenticationSignUp)

            }

        }

    }

}