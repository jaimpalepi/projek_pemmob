package com.example.fesnuk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NookAdapter(
    private val nookList: List<Nook>,
    private val listener: OnExploreButtonClickListener
) : RecyclerView.Adapter<NookAdapter.NookViewHolder>() {

    interface OnExploreButtonClickListener {
        fun onExploreClick(nook: Nook)
    }

    class NookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nookImageView: ImageView = itemView.findViewById(R.id.nookImageView)
        val nookNameTextView: TextView = itemView.findViewById(R.id.nookNameTextView)
        val nookDescriptionTextView: TextView = itemView.findViewById(R.id.nookDescriptionTextView)
        val nookOnlineCountTextView: TextView = itemView.findViewById(R.id.nookOnlineCountTextView)
        val exploreButton: Button = itemView.findViewById(R.id.exploreButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nooks_card, parent, false)
        return NookViewHolder(view)
    }

    override fun getItemCount(): Int {
        return nookList.size
    }

    override fun onBindViewHolder(holder: NookViewHolder, position: Int) {
        val currentNook = nookList[position]

        holder.nookImageView.setImageResource(currentNook.backgroundImage)
        holder.nookNameTextView.text = currentNook.name
        holder.nookDescriptionTextView.text = currentNook.description
        holder.nookOnlineCountTextView.text = "${currentNook.onlineCount} online"

        holder.exploreButton.setOnClickListener {
            listener.onExploreClick(currentNook)
        }
    }
}
