package eu.lukatjee.zorion.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eu.lukatjee.zorion.R
import eu.lukatjee.zorion.views.Chat

class ChatRecyclerViewAdapter(c : Context, messageArray : ArrayList<Chat.Messages>) : RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder>() {

    private val messages = messageArray

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_model_recyclerview_item, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder : ViewHolder, position: Int) {

        holder.chatUsernameTextView.text = messages[position].chatUsername
        holder.chatMessagesTextView.text = messages[position].chatMessage

    }

    override fun getItemCount(): Int {

        return messages.size

    }

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        var chatUsernameTextView : TextView = view.findViewById(R.id.username)
        var chatMessagesTextView : TextView = view.findViewById(R.id.messages)

    }

}