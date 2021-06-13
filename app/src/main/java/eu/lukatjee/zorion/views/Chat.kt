package eu.lukatjee.zorion.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import eu.lukatjee.zorion.R
import eu.lukatjee.zorion.adapters.ChatRecyclerViewAdapter
import eu.lukatjee.zorion.authentication.Profile
import kotlin.collections.ArrayList

class Chat : AppCompatActivity(), View.OnClickListener {

    data class Messages(var chatUsername : String?, var chatMessage : String?)

    private lateinit var realtimeDatabase : FirebaseDatabase
    private lateinit var firebaseStorage : StorageReference
    private lateinit var firebaseFileStorage : StorageReference
    private lateinit var firebaseAuth : FirebaseAuth

    private lateinit var layoutManager : RecyclerView.LayoutManager
    private lateinit var adapter : RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder>

    private lateinit var chatRecyclerView : RecyclerView
    private lateinit var chatProfileButtonImageView : ImageView
    private lateinit var chatInputFieldEditText : EditText
    private lateinit var chatSendButtonImageView : ImageView

    private var messageArray : ArrayList<Messages> = ArrayList()

    private var dataIsLoaded = false
    private var firstRun = true

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        realtimeDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance().reference
        firebaseFileStorage = firebaseStorage.child("profile_${firebaseAuth.currentUser!!.uid}.png")

        layoutManager = LinearLayoutManager(this)
        adapter = ChatRecyclerViewAdapter(this, messageArray)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)

        chatRecyclerView.layoutManager = layoutManager
        chatRecyclerView.adapter = adapter

        if (!dataIsLoaded) {

            FirebaseDatabase.getInstance().reference.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    for (messageSnapshot in snapshot.children) {

                        println(messageSnapshot.key)
                        println(messageSnapshot.child("username").getValue(String::class.java))
                        println(messageSnapshot.child("message").getValue(String::class.java))

                        messageArray.add(Messages(messageSnapshot.child("username").getValue(String::class.java), messageSnapshot.child("message").getValue(String::class.java)))

                    }

                    adapter.notifyDataSetChanged()
                    chatRecyclerView.smoothScrollToPosition(chatRecyclerView.adapter!!.itemCount + 1)

                }

                override fun onCancelled(error: DatabaseError) {

                    TODO("Not yet implemented")

                }

            })

            dataIsLoaded = true

        }

        val lastQuery: Query = FirebaseDatabase.getInstance().reference.orderByKey().limitToLast(1)
        lastQuery.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (!firstRun && dataIsLoaded) {

                    for (messageSnapshot in snapshot.children) {

                        messageArray.add(Messages(messageSnapshot.child("username").getValue(String::class.java), messageSnapshot.child("message").getValue(String::class.java)))

                    }

                    adapter.notifyDataSetChanged()

                } else {

                    firstRun = false

                }

            }

            override fun onCancelled(error: DatabaseError) {

                TODO("Not yet implemented")

            }

        })

        dataIsLoaded = true

        chatProfileButtonImageView = findViewById(R.id.chatProfileButtonImageView)
        chatInputFieldEditText = findViewById(R.id.chatInputFieldEditText)
        chatSendButtonImageView = findViewById(R.id.chatSendButtonImageView)

        chatProfileButtonImageView.setOnClickListener(this)
        chatSendButtonImageView.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.chatProfileButtonImageView -> {

                val authenticationProfile = Intent(this, Profile::class.java)
                startActivity(authenticationProfile)

            }

            R.id.chatSendButtonImageView -> {

                val stringMessage = chatInputFieldEditText.text.toString().trim()
                val stringUsername = firebaseAuth.currentUser!!.displayName

                val messageDataMap = mapOf<String?, String?>("username" to stringUsername, "message" to stringMessage)
                chatInputFieldEditText.text.clear()

                val currentMessageId = realtimeDatabase.reference.push()
                currentMessageId.updateChildren(messageDataMap)

            }

        }

    }

}