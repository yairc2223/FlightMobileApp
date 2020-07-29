package com.example.flightsimapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.server_item.view.*


// this class uses as an adaptor for the server list in DB to the UI recycler view list.
class ServerListAdapter internal constructor(context: Context,var clickListener: OnServerItemClickListener) : RecyclerView.Adapter<ServerListAdapter.ServerListViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var servers = emptyList<ServerEntity>() // Cached copy of words

    inner class ServerListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val serverItemView: TextView = itemView.findViewById(R.id.textView)
        fun initialize(item: ServerEntity,action:OnServerItemClickListener){
            serverItemView.text = item.server_url
            itemView.setOnClickListener {
                action.OnItemClick(item,adapterPosition)
            }
        }
    }
// creates the server recycler view list.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerListViewHolder {
        val itemView = inflater.inflate(R.layout.server_item, parent, false)
        return ServerListViewHolder(itemView)
    }
// creates a connection between the view and the model ot=f the servers.
    override fun onBindViewHolder(holder: ServerListAdapter.ServerListViewHolder, position: Int) {
        holder.initialize(servers.get(position),clickListener)
//        val current = servers[position]
//        holder.serverItemView.text = current.server_url
    }
// sets the server list from the constructor.
    internal fun setServers(words: List<ServerEntity>) {
        this.servers = words
        notifyDataSetChanged()
    }

    override fun getItemCount() = servers.size
}
// the interface of clickable items on list
interface OnServerItemClickListener{
    fun OnItemClick(item : ServerEntity,position: Int)
}