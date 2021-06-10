package eu.lukatjee.zorion.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AlignmentSpan
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

    // Buttons

    private lateinit var signInButtonCircleImageView : CircleImageView
    private lateinit var forgotPasswordButtonCircleImageView : CircleImageView

    // Input fields

    private lateinit var signInEmailEditText : EditText
    private lateinit var signInPasswordEditText : EditText

    // Invalid credentials message

    private lateinit var messageInvalidCredentialsRaw : String
    private lateinit var messageInvalidCredentials : SpannableString

    // Invalid email message

    private lateinit var messageInvalidEmailRaw : String
    private lateinit var messageInvalidEmail : SpannableString

    // Invalid password message

    private lateinit var messageInvalidPasswordRaw : String
    private lateinit var messageInvalidPassword : SpannableString

    // Successful sign in message

    private lateinit var messageSuccessfulSignInRaw : String
    private lateinit var messageSuccessfulSignIn : SpannableString

    // Successful password reset mail message

    private lateinit var messageSuccessfulPasswordResetEmailRaw : String
    private lateinit var messageSuccessfulPasswordResetEmail : SpannableString

    // Invalid user exception message

    private lateinit var messageInvalidUserExceptionRaw : String
    private lateinit var messageInvalidUserException : SpannableString

    // Invalid credentials exception message

    private lateinit var messageInvalidCredentialsExceptionRaw : String
    private lateinit var messageInvalidCredentialsException : SpannableString

    // Network exception message

    private lateinit var messageNetworkExceptionRaw : String
    private lateinit var messageNetworkException : SpannableString

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        firebaseAuth = FirebaseAuth.getInstance()
        intentExtras = intent.extras!!

        signInButtonCircleImageView = findViewById(R.id.signInButtonCircleImageView)
        forgotPasswordButtonCircleImageView = findViewById(R.id.forgotPasswordButtonCircleImageView)

        signInEmailEditText = findViewById(R.id.signInEmailEditText)
        signInPasswordEditText = findViewById(R.id.signInPasswordEditText)

        messageInvalidCredentialsRaw = getString(R.string.zorion_invalid_credentials)
        messageInvalidCredentials = SpannableString(messageInvalidCredentialsRaw)
        messageInvalidCredentials.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, messageInvalidCredentialsRaw.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        messageInvalidEmailRaw = getString(R.string.zorion_invalid_email)
        messageInvalidEmail = SpannableString(messageInvalidEmailRaw)
        messageInvalidEmail.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, messageInvalidEmailRaw.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        messageInvalidPasswordRaw = getString(R.string.zorion_invalid_password)
        messageInvalidPassword = SpannableString(messageInvalidPasswordRaw)
        messageInvalidPassword.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, messageInvalidPasswordRaw.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        messageSuccessfulSignInRaw = getString(R.string.zorion_successful_sign_in)
        messageSuccessfulSignIn = SpannableString(messageSuccessfulSignInRaw)
        messageSuccessfulSignIn.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, messageSuccessfulSignInRaw.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        messageSuccessfulPasswordResetEmailRaw = getString(R.string.zorion_successful_password_reset_email)
        messageSuccessfulPasswordResetEmail = SpannableString(messageSuccessfulPasswordResetEmailRaw)
        messageSuccessfulPasswordResetEmail.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, messageSuccessfulPasswordResetEmailRaw.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        messageInvalidUserExceptionRaw = getString(R.string.zorion_invalid_user_exception)
        messageInvalidUserException = SpannableString(messageInvalidUserExceptionRaw)
        messageInvalidUserException.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, messageInvalidUserExceptionRaw.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        messageInvalidCredentialsExceptionRaw = getString(R.string.zorion_invalid_credentials_exception)
        messageInvalidCredentialsException = SpannableString(messageInvalidCredentialsExceptionRaw)
        messageInvalidCredentialsException.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, messageInvalidCredentialsExceptionRaw.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        messageNetworkExceptionRaw = getString(R.string.zorion_network_exception)
        messageNetworkException = SpannableString(messageNetworkExceptionRaw)
        messageNetworkException.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, messageNetworkExceptionRaw.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        signInButtonCircleImageView.setOnClickListener(this)
        forgotPasswordButtonCircleImageView.setOnClickListener(this)

    }

    override fun onBackPressed() {

        when (intentExtras.getString("FROM_ACTIVITY")) {

            "LANDING" -> {

                val viewLanding = Intent(this, Landing::class.java)

                viewLanding.putExtra("FROM_ACTIVITY", "SIGN_IN")
                viewLanding.putExtra("TO_ACTIVITY", "LANDING")

                startActivity(viewLanding)

            }

            "SIGN_UP" -> {

                val viewLanding = Intent(this, Landing::class.java)

                viewLanding.putExtra("FROM_ACTIVITY", "SIGN_IN")
                viewLanding.putExtra("TO_ACTIVITY", "LANDING")

                startActivity(viewLanding)

            }

        }

    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.signInButtonCircleImageView -> {

                val stringInputEmail = signInEmailEditText.text.toString().trim()
                val stringInputPassword = signInPasswordEditText.text.toString().trim()

                /*

                    In here a bunch of checks take place to make sure
                    all the data the user entered is valid and / or not empty:

                    * email -> empty, a string that does not match an email address
                    * password -> empty

                 */

                if (stringInputEmail.isEmpty() && stringInputPassword.isEmpty()) {

                    Toast.makeText(this, messageInvalidCredentials, Toast.LENGTH_SHORT).show()
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

                    Zorion connects to the firebase authentication service to sign the
                    user in to their account.

                    If for some reason, there's an issue with signing in the user,
                    an exception will be thrown:

                        * unknown user
                        * invalid credentials
                        * network exception

                 */

                signInButtonCircleImageView.setImageResource(R.drawable.authentication_loading)
                signInButtonCircleImageView.isEnabled = false

                firebaseAuth.signInWithEmailAndPassword(stringInputEmail, stringInputPassword).addOnCompleteListener { signInTask ->

                    if (signInTask.isSuccessful) {

                        signInButtonCircleImageView.setImageResource(R.drawable.authentication_arrow)
                        signInButtonCircleImageView.isEnabled = true

                        Toast.makeText(this, messageSuccessfulSignIn, Toast.LENGTH_SHORT).show()

                    } else {

                        signInButtonCircleImageView.setImageResource(R.drawable.authentication_arrow)
                        signInButtonCircleImageView.isEnabled = true

                        try {

                            throw signInTask.exception!!

                        } catch (invalidUser : FirebaseAuthInvalidUserException) {

                            Toast.makeText(this, messageInvalidUserException, Toast.LENGTH_SHORT).show()

                        } catch (invalidCredentials : FirebaseAuthInvalidCredentialsException) {

                            Toast.makeText(this, messageInvalidCredentialsException, Toast.LENGTH_SHORT).show()

                        } catch (webException : FirebaseNetworkException) {

                            Toast.makeText(this, messageNetworkException, Toast.LENGTH_SHORT).show()

                        }

                    }

                }

            }

            R.id.forgotPasswordButtonCircleImageView -> {

                val stringInputEmail = signInEmailEditText.text.toString().trim()

                /*

                    In here a bunch of checks take place to make sure
                    all the data the user entered is valid and / or not empty:

                    * email -> empty, a string that does not match an email address

                 */

                if (stringInputEmail.isEmpty()  || !Patterns.EMAIL_ADDRESS.matcher(stringInputEmail).matches()) {

                    Toast.makeText(this, messageInvalidEmail, Toast.LENGTH_SHORT).show()
                    return

                }

                /*

                    Zorion connects to the firebase authentication service to send a
                    password reset email to the user.

                    If for some reason, there's an issue with signing in the user,
                    an exception will be thrown:

                        * unknown user
                        * network exception

                 */

                forgotPasswordButtonCircleImageView.setImageResource(R.drawable.authentication_loading)
                forgotPasswordButtonCircleImageView.isEnabled = false

                firebaseAuth.sendPasswordResetEmail(stringInputEmail).addOnCompleteListener { sendPasswordResetEmailTask ->

                    if (sendPasswordResetEmailTask.isSuccessful) {

                        Toast.makeText(this, messageSuccessfulPasswordResetEmail, Toast.LENGTH_SHORT).show()

                        forgotPasswordButtonCircleImageView.setImageResource(R.drawable.authentication_reset_password)
                        forgotPasswordButtonCircleImageView.isEnabled = true

                    } else {

                        forgotPasswordButtonCircleImageView.setImageResource(R.drawable.authentication_reset_password)
                        forgotPasswordButtonCircleImageView.isEnabled = true

                        try {

                            throw sendPasswordResetEmailTask.exception!!

                        } catch (invalidUser : FirebaseAuthInvalidUserException) {

                            Toast.makeText(this, messageInvalidUserException, Toast.LENGTH_SHORT).show()

                        } catch (webException : FirebaseNetworkException) {

                            Toast.makeText(this, messageNetworkException, Toast.LENGTH_SHORT).show()

                        }

                    }

                }

            }

        }

    }

}