package com.example.fesnuk

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fesnuk.databinding.ActivityNooksBinding
import com.example.fesnuk.ui.NookViewModel

class NooksActivity : AppCompatActivity(), PostAdapter.OnPostClickListener {

    private lateinit var binding: ActivityNooksBinding
    private lateinit var postAdapter: PostAdapter
    private val nookViewModel: NookViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNooksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        nookViewModel.posts.observe(this, Observer {
            postAdapter.submitList(it)
        })

        nookViewModel.loadPosts()

        binding.toolbar.findViewById<android.widget.ImageView>(R.id.buttonClose).setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(this)
        binding.postsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@NooksActivity)
            adapter = postAdapter
        }
    }

    override fun onPostClick(post: Post) {
        val intent = Intent(this, ThreadActivity::class.java)
        startActivity(intent)
    }
}
