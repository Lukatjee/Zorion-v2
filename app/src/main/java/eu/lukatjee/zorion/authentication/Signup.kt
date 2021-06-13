package eu.lukatjee.zorion.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import eu.lukatjee.zorion.R
import eu.lukatjee.zorion.views.Landing


class Signup : AppCompatActivity(), View.OnClickListener {

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var intentExtras : Bundle
    private lateinit var signUpButtonCircleImageView : CircleImageView
    private lateinit var signUpUsernameEditText : EditText
    private lateinit var signUpEmailEditText : EditText
    private lateinit var signUpPasswordEditText : EditText
    private lateinit var messageStrings : List<String>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        firebaseAuth = Firebase.auth
        intentExtras = intent.extras!!

        signUpButtonCircleImageView = findViewById(R.id.signUpButtonCircleImageView)
        signUpUsernameEditText = findViewById(R.id.signUpUsernameEditText)
        signUpEmailEditText = findViewById(R.id.signUpEmailEditText)
        signUpPasswordEditText = findViewById(R.id.signUpPasswordEditText)

        signUpButtonCircleImageView.setOnClickListener(this)

        /*

            @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

                           Messages

            @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

            0. Invalid credential input
            1. Invalid username input
            2. Invalid email input
            3. Invalid password input
            4. Signed up successfully
            5. Weak password exception
            6. User collision exception
            7. Network exception

            @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

         */

        messageStrings = listOf(getString(R.string.authentication_message_invalid_credentials), getString(R.string.authentication_message_invalid_username), getString(R.string.authentication_message_invalid_email), getString(R.string.authentication_message_invalid_password), getString(R.string.authentication_message_successful_sign_up), getString(R.string.authentication_exception_weak_password), getString(R.string.authentication_exception_user_collision), getString(R.string.authentication_exception_network))

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

            R.id.signUpButtonCircleImageView -> {

                val stringInputUsername = signUpUsernameEditText.text.toString().trim()
                val stringInputEmail = signUpEmailEditText.text.toString().trim()
                val stringInputPassword = signUpPasswordEditText.text.toString().trim()

                signUpButtonCircleImageView.setImageResource(R.drawable.authentication_icon_loading)
                signUpButtonCircleImageView.isEnabled = false

                if (stringInputUsername.isEmpty() && stringInputEmail.isEmpty() && stringInputPassword.isEmpty()) {

                    signUpButtonCircleImageView.setImageResource(R.drawable.authentication_icon_arrow)
                    signUpButtonCircleImageView.isEnabled = true

                    Toast.makeText(this, messageStrings[0], Toast.LENGTH_SHORT).show()
                    return

                }

                if (stringInputUsername.isEmpty()) {

                    signUpButtonCircleImageView.setImageResource(R.drawable.authentication_icon_arrow)
                    signUpButtonCircleImageView.isEnabled = true

                    Toast.makeText(this, messageStrings[1], Toast.LENGTH_SHORT).show()
                    return

                }

                if (stringInputEmail.isEmpty()  || !Patterns.EMAIL_ADDRESS.matcher(stringInputEmail).matches()) {

                    signUpButtonCircleImageView.setImageResource(R.drawable.authentication_icon_arrow)
                    signUpButtonCircleImageView.isEnabled = true

                    Toast.makeText(this, messageStrings[2], Toast.LENGTH_SHORT).show()
                    return

                }

                if (stringInputPassword.isEmpty()) {

                    signUpButtonCircleImageView.setImageResource(R.drawable.authentication_icon_arrow)
                    signUpButtonCircleImageView.isEnabled = true

                    Toast.makeText(this, messageStrings[3], Toast.LENGTH_SHORT).show()
                    return

                }

                firebaseAuth.createUserWithEmailAndPassword(stringInputEmail, stringInputPassword).addOnCompleteListener { taskCreate ->

                    if (taskCreate.isSuccessful) {

                        Toast.makeText(this, messageStrings[4], Toast.LENGTH_SHORT).show()

                        firebaseAuth.signInWithEmailAndPassword(stringInputEmail, stringInputPassword).addOnCompleteListener { taskSignIn ->

                            if (taskSignIn.isSuccessful) {

                                val currentlySignedInUser = firebaseAuth.currentUser!!
                                val profileChangeRequest = UserProfileChangeRequest.Builder().setDisplayName(stringInputUsername).build()

                                currentlySignedInUser.updateProfile(profileChangeRequest).addOnCompleteListener { taskUpdate ->

                                    firebaseAuth.signOut()

                                    if (taskUpdate.isSuccessful) {

                                        signUpButtonCircleImageView.setImageResource(R.drawable.authentication_icon_arrow)
                                        signUpButtonCircleImageView.isEnabled = true

                                        val authenticationSignIn = Intent(this, Signin::class.java)
                                        startActivity(authenticationSignIn)

                                    } else {

                                        signUpButtonCircleImageView.setImageResource(R.drawable.authentication_icon_arrow)
                                        signUpButtonCircleImageView.isEnabled = true

                                        try {

                                            throw taskSignIn.exception!!

                                        } catch (exceptionNetwork : FirebaseNetworkException) {

                                            Toast.makeText(this, messageStrings[7], Toast.LENGTH_SHORT).show()

                                        }

                                    }

                                }

                            } else {

                                signUpButtonCircleImageView.setImageResource(R.drawable.authentication_icon_arrow)
                                signUpButtonCircleImageView.isEnabled = true

                                try {

                                    throw taskSignIn.exception!!

                                } catch (exceptionNetwork : FirebaseNetworkException) {

                                    Toast.makeText(this, messageStrings[7], Toast.LENGTH_SHORT).show()

                                }

                            }

                        }

                    } else {

                        signUpButtonCircleImageView.setImageResource(R.drawable.authentication_icon_arrow)
                        signUpButtonCircleImageView.isEnabled = true

                        try {

                            throw taskCreate.exception!!

                        } catch (exceptionWeakPassword : FirebaseAuthWeakPasswordException) {

                            Toast.makeText(this, messageStrings[5], Toast.LENGTH_SHORT).show()

                        } catch (exceptionUserCollision : FirebaseAuthUserCollisionException) {

                            Toast.makeText(this, messageStrings[6], Toast.LENGTH_SHORT).show()

                        } catch (exceptionNetwork : FirebaseNetworkException) {

                            Toast.makeText(this, messageStrings[7], Toast.LENGTH_SHORT).show()

                        }

                    }

                }

            }

        }

    }

}