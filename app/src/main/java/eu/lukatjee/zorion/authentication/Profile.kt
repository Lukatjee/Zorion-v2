package eu.lukatjee.zorion.authentication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import eu.lukatjee.zorion.R
import eu.lukatjee.zorion.views.Chat
import eu.lukatjee.zorion.views.Landing

class Profile : AppCompatActivity(), View.OnClickListener {

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var firebaseStorage : StorageReference
    private lateinit var firebaseFileStorage : StorageReference

    private lateinit var profilePictureCircleImageView : CircleImageView
    private lateinit var profileUsernameTextView : TextView
    private lateinit var profileEmailTextView : TextView

    private lateinit var profileButtonEditImageView : ImageView
    private lateinit var profileButtonChatImageView : ImageView
    private lateinit var profileButtonLogoutImageView : ImageView

    private lateinit var messageStrings : List<String>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance().reference
        firebaseFileStorage = firebaseStorage.child("profile_${firebaseAuth.currentUser!!.uid}.png")

        profilePictureCircleImageView = findViewById(R.id.profilePictureCircleImageView)
        profileUsernameTextView = findViewById(R.id.profileUserdataUsernameDisplayTextView)
        profileEmailTextView = findViewById(R.id.profileUserdataEmailDisplayTextView)

        profileButtonEditImageView = findViewById(R.id.profileButtonEditCircleImageView)
        profileButtonChatImageView = findViewById(R.id.profileButtonChatCircleImageView)
        profileButtonLogoutImageView = findViewById(R.id.profileButtonLogoutCircleImageView)

        profileEmailTextView.movementMethod = ScrollingMovementMethod()
        profileEmailTextView.setHorizontallyScrolling(true)

        profilePictureCircleImageView.setOnClickListener(this)
        profileButtonEditImageView.setOnClickListener(this)
        profileButtonChatImageView.setOnClickListener(this)
        profileButtonLogoutImageView.setOnClickListener(this)

        /*

            @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

                           Messages

            @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

            0. Updated username
            1. Updated email
            2. Network exception
            3. Invalid username
            4. Invalid email
            5. User collision exception
            6. Exit application

            @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

         */

        messageStrings = listOf(getString(R.string.profile_message_username_changed), getString(R.string.profile_message_email_changed), getString(R.string.authentication_exception_network), getString(R.string.authentication_message_invalid_username), getString(R.string.authentication_message_invalid_email), getString(R.string.authentication_exception_user_collision), getString(R.string.profile_message_exit_application))
        updateEmail(); updateUsername(); updateProfile()

    }

    private fun updateProfile() {

        firebaseFileStorage.downloadUrl.addOnCompleteListener {

            if (it.isSuccessful) {

                Picasso.get().load(it.result).into(profilePictureCircleImageView)

            } else {

                try {

                    throw it.exception!!

                } catch (exceptionStorage : StorageException) {}

            }

        }

    }

    private fun updateEmail() {

        val userDataEmail = firebaseAuth.currentUser!!.email
        profileEmailTextView.text = (userDataEmail)
        profileEmailTextView.invalidate()

    }

    private fun updateUsername() {

        val userDataDisplayName = firebaseAuth.currentUser!!.displayName
        profileUsernameTextView.text = userDataDisplayName
        profileUsernameTextView.invalidate()


    }

    private var lastPressed : Long = 0

    override fun onBackPressed() {

        if (lastPressed + 2000 > System.currentTimeMillis()) {

            firebaseAuth.signOut()
            finishAffinity()

        } else {

            Toast.makeText(this, messageStrings[6], Toast.LENGTH_SHORT).show()
            lastPressed = System.currentTimeMillis()

        }

    }

    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri : Uri? ->

        uri?.let {

            firebaseFileStorage.putFile(uri).addOnCompleteListener {

                if (it.isSuccessful) {

                    Picasso.get().load(uri).into(profilePictureCircleImageView)

                } else {

                    try {

                        throw it.exception!!

                    } catch (exceptionNetwork : FirebaseNetworkException) {

                        Toast.makeText(this, messageStrings[2], Toast.LENGTH_SHORT).show()

                    }

                }

            }

        }

    }

    private var profileIsEditing = false
    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.profilePictureCircleImageView -> {

                if (profileIsEditing) {

                    selectImageFromGalleryResult.launch("image/*")

                }

            }

            R.id.profileButtonEditCircleImageView -> {

                if (!profileIsEditing) {

                    profileIsEditing = true

                    profilePictureCircleImageView.isClickable = true
                    profileUsernameTextView.isEnabled = true
                    profileEmailTextView.isEnabled = true

                    profileUsernameTextView.isClickable = true
                    profileEmailTextView.isClickable = true

                    profileButtonEditImageView.setImageResource(R.drawable.profile_icon_save)

                } else {

                    profileIsEditing = false

                    profilePictureCircleImageView.isClickable = false
                    profileUsernameTextView.isEnabled = false
                    profileEmailTextView.isEnabled = false

                    profileUsernameTextView.isClickable = false
                    profileEmailTextView.isClickable = false

                    val stringInputUsername = profileUsernameTextView.text.toString().trim()
                    val stringInputEmail = profileEmailTextView.text.toString().trim()

                    val currentUserUsername = firebaseAuth.currentUser!!.displayName
                    val currentUserEmail = firebaseAuth.currentUser!!.email

                    profileButtonEditImageView.setImageResource(R.drawable.authentication_icon_loading)

                    if (stringInputUsername != currentUserUsername && stringInputUsername.isNotEmpty()) {

                        val profileChangeRequest = UserProfileChangeRequest.Builder().setDisplayName(stringInputUsername).build()

                        firebaseAuth.currentUser!!.updateProfile(profileChangeRequest).addOnCompleteListener { updateUsernameTask ->

                                if (updateUsernameTask.isSuccessful) {

                                    Toast.makeText(this, messageStrings[0], Toast.LENGTH_SHORT).show()
                                    updateUsername()

                                } else {

                                    updateUsername()

                                    try {

                                        throw updateUsernameTask.exception!!

                                    } catch (networkException: FirebaseNetworkException) {

                                        Toast.makeText(this, messageStrings[2], Toast.LENGTH_SHORT).show()

                                    }

                                }

                            }

                    } else if (stringInputUsername.isEmpty()) {

                        Toast.makeText(this, messageStrings[3], Toast.LENGTH_SHORT).show()
                        return

                    }

                    if (stringInputEmail != currentUserEmail && Patterns.EMAIL_ADDRESS.matcher(stringInputEmail).matches() && stringInputEmail.isNotEmpty()) {

                        firebaseAuth.currentUser!!.updateEmail(stringInputEmail).addOnCompleteListener { updateEmailTask ->

                                if (updateEmailTask.isSuccessful) {

                                    Toast.makeText(this, messageStrings[1], Toast.LENGTH_SHORT).show()
                                    updateEmail()

                                } else {

                                    updateEmail()

                                    try {

                                        throw updateEmailTask.exception!!

                                    } catch (userCollisionException: FirebaseAuthUserCollisionException) {

                                        Toast.makeText(this, messageStrings[5], Toast.LENGTH_SHORT).show()

                                    } catch (networkException: FirebaseNetworkException) {

                                        Toast.makeText(this, messageStrings[2], Toast.LENGTH_SHORT).show()

                                    }

                                }

                            }

                    } else if (!Patterns.EMAIL_ADDRESS.matcher(stringInputEmail).matches() || stringInputEmail.isEmpty()) {

                        Toast.makeText(this, messageStrings[4], Toast.LENGTH_SHORT).show()
                        return

                    }

                    profileButtonEditImageView.setImageResource(R.drawable.profile_icon_edit)

                }

            }

            R.id.profileButtonChatCircleImageView -> {

                val viewChat = Intent(this, Chat::class.java)
                startActivity(viewChat)

            }

            R.id.profileButtonLogoutCircleImageView -> {

                firebaseAuth.signOut()

                val viewLanding = Intent(this, Landing::class.java)
                startActivity(viewLanding)

            }

        }

    }

}