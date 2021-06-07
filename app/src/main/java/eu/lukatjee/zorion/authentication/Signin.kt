package eu.lukatjee.zorion.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import eu.lukatjee.zorion.R
import eu.lukatjee.zorion.views.Landing

class Signin : AppCompatActivity() {

    private lateinit var intentExtras : Bundle

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        intentExtras = intent.extras!!

    }

    override fun onBackPressed() {

        when (intentExtras.getString("FROM_ACTIVITY")) {

            "LANDING" -> {

                var viewLanding = Intent(this, Landing::class.java)

                viewLanding.putExtra("FROM_ACTIVITY", "SIGN_IN")
                viewLanding.putExtra("TO_ACTIVITY", "LANDING")

                startActivity(viewLanding)

            }

            "SIGN_UP" -> {

                var viewLanding = Intent(this, Landing::class.java)

                viewLanding.putExtra("FROM_ACTIVITY", "SIGN_IN")
                viewLanding.putExtra("TO_ACTIVITY", "LANDING")

                startActivity(viewLanding)

            }

        }

    }

}