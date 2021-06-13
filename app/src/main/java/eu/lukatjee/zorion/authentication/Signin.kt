package eu.lukatjee.zorion.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import de.hdodenhof.circleimageview.CircleImageView
import eu.lukatjee.zorion.R
import eu.lukatjee.zorion.views.Landing

class Signin : AppCompatActivity(), View.OnClickListener {

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var intentExtras : Bundle
    private lateinit var signInButtonCircleImageView : CircleImageView
    private lateinit var secureButtonCircleImageView : CircleImageView
    private lateinit var signInEmailEditText : EditText
    private lateinit var signInPasswordEditText : EditText
    private lateinit var messageStrings : List<String>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        firebaseAuth = FirebaseAuth.getInstance()
        intentExtras = intent.extras!!

        signInButtonCircleImageView = findViewById(R.id.signInButtonCircleImageView)
        secureButtonCircleImageView = findViewById(R.id.signInPasswordRecoveryButtonCircleImageView)
        signInEmailEditText = findViewById(R.id.signInEmailEditText)
        signInPasswordEditText = findViewById(R.id.signInPasswordEditText)

        signInButtonCircleImageView.setOnClickListener(this)
        secureButtonCircleImageView.setOnClickListener(this)

        /*

            @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

                           Messages

            @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

            0. Invalid credential input
            1. Invalid email input
            2. Invalid password input
            3. Signed in successfully
            4. Sent password reset successfully
            5. Invalid user exception
            6. Invalid credentials exception
            7. Network exception

            @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

         */

        messageStrings = listOf(getString(R.string.authentication_message_invalid_credentials), getString(R.string.authentication_message_invalid_email), getString(R.string.authentication_message_invalid_password), getString(R.string.authentication_message_successful_sign_in), getString(R.string.authentication_message_password_reset_email), getString(R.string.authentication_exception_invalid_user), getString(R.string.authentication_exception_incorrect_credentials), getString(R.string.authentication_exception_network)

        )

    }

    override fun onBackPressed() {

        when (intentExtras.getString("FROM_ACTIVITY")) {

            else -> {

                val viewLanding = Intent(this, Landing::class.java)
                startActivity(viewLanding)

            }

        }

    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.signInButtonCircleImageView -> {

                val stringInputEmail = signInEmailEditText.text.toString().trim()
                val stringInputPassword = signInPasswordEditText.text.toString().trim()

                if (stringInputEmail.isEmpty() && stringInputPassword.isEmpty()) {

                    Toast.makeText(this, messageStrings[0], Toast.LENGTH_SHORT).show()
                    return

                }

                if (stringInputEmail.isEmpty()  || !Patterns.EMAIL_ADDRESS.matcher(stringInputEmail).matches()) {

                    Toast.makeText(this, messageStrings[1], Toast.LENGTH_SHORT).show()
                    return

                }

                if (stringInputPassword.isEmpty()) {

                    Toast.makeText(this, messageStrings[2], Toast.LENGTH_SHORT).show()
                    return

                }

                signInButtonCircleImageView.setImageResource(R.drawable.authentication_icon_loading)
                signInButtonCircleImageView.isEnabled = false

                firebaseAuth.signInWithEmailAndPassword(stringInputEmail, stringInputPassword).addOnCompleteListener { taskSignIn ->

                    if (taskSignIn.isSuccessful) {

                        signInButtonCircleImageView.setImageResource(R.drawable.authentication_icon_arrow)
                        signInButtonCircleImageView.isEnabled = true

                        Toast.makeText(this, messageStrings[3], Toast.LENGTH_SHORT).show()

                        val profileIntent = Intent(this, Profile::class.java)
                        startActivity(profileIntent)

                    } else {

                        signInButtonCircleImageView.setImageResource(R.drawable.authentication_icon_arrow)
                        signInButtonCircleImageView.isEnabled = true

                        try {

                            throw taskSignIn.exception!!

                        } catch (exceptionInvalidUser : FirebaseAuthInvalidUserException) {

                            Toast.makeText(this, messageStrings[5], Toast.LENGTH_SHORT).show()

                        } catch (exceptionInvalidCredentials : FirebaseAuthInvalidCredentialsException) {

                            Toast.makeText(this, messageStrings[6], Toast.LENGTH_SHORT).show()

                        } catch (exceptionNetwork : FirebaseNetworkException) {

                            Toast.makeText(this, messageStrings[7], Toast.LENGTH_SHORT).show()

                        }

                    }

                }

            }

            R.id.signInPasswordRecoveryButtonCircleImageView -> {

                val stringInputEmail = signInEmailEditText.text.toString().trim()

                if (stringInputEmail.isEmpty()  || !Patterns.EMAIL_ADDRESS.matcher(stringInputEmail).matches()) {

                    Toast.makeText(this, messageStrings[1], Toast.LENGTH_SHORT).show()
                    return

                }

                secureButtonCircleImageView.setImageResource(R.drawable.authentication_icon_loading)
                secureButtonCircleImageView.isEnabled = false

                firebaseAuth.sendPasswordResetEmail(stringInputEmail).addOnCompleteListener { taskPasswordRecovery ->

                    if (taskPasswordRecovery.isSuccessful) {

                        Toast.makeText(this, messageStrings[4], Toast.LENGTH_SHORT).show()

                        secureButtonCircleImageView.setImageResource(R.drawable.authentication_icon_reset_password)
                        secureButtonCircleImageView.isEnabled = true

                    } else {

                        secureButtonCircleImageView.setImageResource(R.drawable.authentication_icon_reset_password)
                        secureButtonCircleImageView.isEnabled = true

                        try {

                            throw taskPasswordRecovery.exception!!

                        } catch (exceptionInvalidUser : FirebaseAuthInvalidUserException) {

                            Toast.makeText(this, messageStrings[5], Toast.LENGTH_SHORT).show()

                        } catch (exceptionNetwork : FirebaseNetworkException) {

                            Toast.makeText(this, messageStrings[7], Toast.LENGTH_SHORT).show()

                        }

                    }

                }

            }

        }

    }

}