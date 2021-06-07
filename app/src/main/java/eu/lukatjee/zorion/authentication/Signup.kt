package eu.lukatjee.zorion.authentication

import android.content.Intent
import android.os.Bundle
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AlignmentSpan
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
    private lateinit var signUpProgressBar : CircleImageView

    // Input fields

    private lateinit var signUpUsernameEditText : EditText
    private lateinit var signUpEmailEditText : EditText
    private lateinit var signUpPasswordEditText : EditText

    // Invalid credentials message

    private lateinit var messageInvalidCredentialsRaw : String
    private lateinit var messageInvalidCredentials : SpannableString

    // Invalid username message

    private lateinit var messageInvalidUsernameRaw : String
    private lateinit var messageInvalidUsername : SpannableString

    // Invalid email message

    private lateinit var messageInvalidEmailRaw : String
    private lateinit var messageInvalidEmail : SpannableString

    // Invalid password message

    private lateinit var messageInvalidPasswordRaw : String
    private lateinit var messageInvalidPassword : SpannableString

    // Successful sign up message

    private lateinit var messageSuccessfulSignUpRaw : String
    private lateinit var messageSuccessfulSignUp : SpannableString

    // Weak password exception message

    private lateinit var messageWeakPasswordExceptionRaw : String
    private lateinit var messageWeakPasswordException : SpannableString

    // User collision exception message

    private lateinit var messageUserCollisionExceptionRaw : String
    private lateinit var messageUserCollisionException : SpannableString

    // Network exception message

    private lateinit var messageNetworkExceptionRaw : String
    private lateinit var messageNetworkException : SpannableString

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        firebaseAuth = Firebase.auth
        intentExtras = intent.extras!!

        signUpButtonCircleImageView = findViewById(R.id.signUpButtonCircleImageView)
        signUpProgressBar = findViewById(R.id.signUpCircleImageView)

        signUpUsernameEditText = findViewById(R.id.signUpUsernameEditText)
        signUpEmailEditText = findViewById(R.id.signUpEmailEditText)
        signUpPasswordEditText = findViewById(R.id.signUpPasswordEditText)

        /*

            Here all messages are being centered so they can be used
            in toast messages and not look weird.

         */

        messageInvalidCredentialsRaw = getString(R.string.zorion_invalid_credentials)
        messageInvalidCredentials = SpannableString(messageInvalidCredentialsRaw)
        messageInvalidCredentials.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, messageInvalidCredentialsRaw.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        messageInvalidUsernameRaw = getString(R.string.zorion_invalid_username)
        messageInvalidUsername = SpannableString(messageInvalidUsernameRaw)
        messageInvalidUsername.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, messageInvalidUsernameRaw.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        messageInvalidEmailRaw = getString(R.string.zorion_invalid_email)
        messageInvalidEmail = SpannableString(messageInvalidEmailRaw)
        messageInvalidEmail.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, messageInvalidEmailRaw.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        messageInvalidPasswordRaw = getString(R.string.zorion_invalid_password)
        messageInvalidPassword = SpannableString(messageInvalidPasswordRaw)
        messageInvalidPassword.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, messageInvalidPasswordRaw.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        messageSuccessfulSignUpRaw = getString(R.string.zorion_successful_sign_up)
        messageSuccessfulSignUp = SpannableString(messageSuccessfulSignUpRaw)
        messageSuccessfulSignUp.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, messageSuccessfulSignUpRaw.length -1, Spannable.SPAN_INCLUSIVE_INCLUSIVE )

        messageWeakPasswordExceptionRaw = getString(R.string.zorion_weak_password_exception)
        messageWeakPasswordException = SpannableString(messageWeakPasswordExceptionRaw)
        messageWeakPasswordException.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, messageWeakPasswordExceptionRaw.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        messageUserCollisionExceptionRaw = getString(R.string.zorion_user_collision_exception)
        messageUserCollisionException = SpannableString(messageUserCollisionExceptionRaw)
        messageUserCollisionException.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, messageUserCollisionExceptionRaw.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        messageNetworkExceptionRaw = getString(R.string.zorion_network_exception)
        messageNetworkException = SpannableString(messageNetworkExceptionRaw)
        messageNetworkException.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, messageNetworkExceptionRaw.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        signUpButtonCircleImageView.setOnClickListener(this)

    }

    /*

        Handle the onBackPressed event, this will direct the user to
        the correct view based on what their previous and current view is.

     */

    override fun onBackPressed() {

        when (intentExtras.getString("FROM_ACTIVITY")) {

            "LANDING" -> {

                val viewLanding = Intent(this, Landing::class.java)

                viewLanding.putExtra("FROM_ACTIVITY", "SIGN_UP")
                viewLanding.putExtra("TO_ACTIVITY", "LANDING")

                startActivity(viewLanding)

            }

        }

    }

    /*

        Handle all onClick events, those are assigned in the
        onCreate function

     */

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.signUpButtonCircleImageView -> {

                val stringInputUsername = signUpUsernameEditText.text.toString().trim()
                val stringInputEmail = signUpEmailEditText.text.toString().trim()
                val stringInputPassword = signUpPasswordEditText.text.toString().trim()

                /*

                    In here a bunch of checks take place to make sure
                    all the data the user entered is valid and / or not empty:

                    * username -> empty
                    * email -> empty, a string that does not match an email address
                    * password -> empty

                 */

                if (stringInputUsername.isEmpty() && stringInputEmail.isEmpty() && stringInputPassword.isEmpty()) {

                    Toast.makeText(this, messageInvalidCredentials, Toast.LENGTH_SHORT).show()
                    return

                }

                if (stringInputUsername.isEmpty()) {

                    Toast.makeText(this, messageInvalidUsername, Toast.LENGTH_SHORT).show()
                    return

                }

                if (stringInputEmail.isEmpty()  || !Patterns.EMAIL_ADDRESS.matcher(stringInputEmail).matches()) {

                    Toast.makeText(this, messageInvalidEmail, Toast.LENGTH_SHORT).show()
                    return

                }

                if (stringInputPassword.isEmpty()) {

                    Toast.makeText(this, messageInvalidPassword, Toast.LENGTH_SHORT).show()
                    return

                }

                /*

                    Zorion connects to the firebase authentication service to create
                    an account with the given credentials

                    If for some reason, there's an issue with signing up the user,
                    an exception will be thrown:

                        * weak password exception
                        * user collision exception
                        * network exception

                 */

                signUpProgressBar.visibility = View.VISIBLE
                signUpButtonCircleImageView.visibility = View.GONE

                firebaseAuth.createUserWithEmailAndPassword(stringInputEmail, stringInputPassword).addOnCompleteListener { createAccountTask ->

                    if (createAccountTask.isSuccessful) {

                        signUpProgressBar.visibility = View.GONE
                        signUpButtonCircleImageView.visibility = View.VISIBLE

                        Toast.makeText(this, messageSuccessfulSignUp, Toast.LENGTH_SHORT).show()

                        /*

                            Once the sign up task has been completed, Zorion will temporarily sign the user in
                            to apply a profile update to initialize the displayname (or username).

                            If for some reason, there's an issue with signing up the user,
                            an exception will be thrown:

                                * network exception

                         */

                        signUpProgressBar.visibility = View.VISIBLE
                        signUpButtonCircleImageView.visibility = View.GONE

                        firebaseAuth.signInWithEmailAndPassword(stringInputEmail, stringInputPassword).addOnCompleteListener { signInTask ->

                            if (signInTask.isSuccessful) {

                                signUpProgressBar.visibility = View.GONE
                                signUpButtonCircleImageView.visibility = View.VISIBLE

                                val currentlySignedInUser = firebaseAuth.currentUser!!
                                val profileChangeRequest = UserProfileChangeRequest.Builder().setDisplayName(stringInputUsername).build()

                                /*

                                    Final step of the sign up process where the profile will be updated
                                    with the username and the user will be signed out (even if it fails, security reasons).

                                    If for some reason, there's an issue with updating the
                                    user profile, an exception will be thrown:

                                        * network exception

                                */

                                signUpProgressBar.visibility = View.VISIBLE
                                signUpButtonCircleImageView.visibility = View.GONE

                                currentlySignedInUser.updateProfile(profileChangeRequest).addOnCompleteListener { updateProfileTask ->

                                    firebaseAuth.signOut()

                                    if (updateProfileTask.isSuccessful) {

                                        signUpProgressBar.visibility = View.GONE
                                        signUpButtonCircleImageView.visibility = View.VISIBLE

                                        val authenticationSignIn = Intent(this, Signin::class.java)

                                        authenticationSignIn.putExtra("FROM_ACTIVITY", "SIGN_UP")
                                        authenticationSignIn.putExtra("TO_ACTIVITY", "SIGN_IN")

                                        startActivity(authenticationSignIn)

                                    } else {

                                        signUpProgressBar.visibility = View.GONE
                                        signUpButtonCircleImageView.visibility = View.VISIBLE

                                        try {

                                            throw signInTask.exception!!

                                        } catch (webException : FirebaseNetworkException) {

                                            Toast.makeText(this, messageNetworkException, Toast.LENGTH_SHORT).show()

                                        }

                                    }

                                }

                            } else {

                                signUpProgressBar.visibility = View.GONE
                                signUpButtonCircleImageView.visibility = View.VISIBLE

                                try {

                                    throw signInTask.exception!!

                                } catch (webException : FirebaseNetworkException) {

                                    Toast.makeText(this, messageNetworkException, Toast.LENGTH_SHORT).show()

                                }

                            }

                        }

                    } else {

                        signUpProgressBar.visibility = View.GONE
                        signUpButtonCircleImageView.visibility = View.VISIBLE

                        try {

                            throw createAccountTask.exception!!

                        } catch (weakPasswordException : FirebaseAuthWeakPasswordException) {

                            Toast.makeText(this, messageWeakPasswordException, Toast.LENGTH_SHORT).show()

                        } catch (userExists : FirebaseAuthUserCollisionException) {

                            Toast.makeText(this, messageUserCollisionException, Toast.LENGTH_SHORT).show()

                        } catch (webException : FirebaseNetworkException) {

                            Toast.makeText(this, messageNetworkException, Toast.LENGTH_SHORT).show()

                        }

                    }

                }

            }

        }

    }

}