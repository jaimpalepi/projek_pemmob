package com.example.fesnuk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(
    private val listener: OnPostClickListener
) : ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback()) {

    interface OnPostClickListener {
        fun onPostClick(post: Post)
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ID diperbarui agar sesuai dengan item_thread_original_post.xml
        val nookTextView: TextView = itemView.findViewById(R.id.nook_name_text)
        val timeAgoTextView: TextView = itemView.findViewById(R.id.date_text) // Menggunakan date_text untuk timeAgo
        val titleTextView: TextView = itemView.findViewById(R.id.thread_title_text)
        val postImageView: ImageView = itemView.findViewById(R.id.post_image)
        val captionTextView: TextView = itemView.findViewById(R.id.post_content_text)
        val replyCountTextView: TextView = itemView.findViewById(R.id.reply_count_text)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val post = getItem(position)
                    listener.onPostClick(post)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_thread_original_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)

        // Mengikat data ke View yang sudah benar
        holder.nookTextView.text = post.nook
        holder.timeAgoTextView.text = "· ${post.timeAgo}" // Menambahkan format "· "
        holder.titleTextView.text = post.title
        holder.captionTextView.text = post.caption
        holder.replyCountTextView.text = post.replyCount.toString()

        if (post.postImage != null) {
            holder.postImageView.setImageResource(post.postImage)
            holder.postImageView.visibility = View.VISIBLE
        } else {
            holder.postImageView.visibility = View.GONE
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}
