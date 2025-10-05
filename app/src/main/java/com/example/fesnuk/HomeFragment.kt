package com.example.fesnuk

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment(), PostAdapter.OnPostClickListener {

    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postsRecyclerView: RecyclerView = view.findViewById(R.id.postsRecyclerView)
        postAdapter = PostAdapter(this)

        postsRecyclerView.adapter = postAdapter
        postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadPosts()
    }

    private fun loadPosts() {
        val posts = listOf(
            Post(
                nook = "::AnimeMemes",
                timeAgo = "51m ago",
                title = "BAKUSHINNNN!!!!!!!",
                caption = "Are you bakumaxxing rn?",
                postImage = R.drawable.placeholder_image,
                replyCount = 45
            ),
            Post(
                nook = "::AnimeMemes",
                timeAgo = "51m ago",
                title = "How much money have Kazuya spent on renting?",
                caption = "I assume one morbillion yen or so...",
                postImage = null,
                replyCount = 45
            )
        )
        postAdapter.submitList(posts)
    }

    override fun onPostClick(post: Post) {
        val intent = Intent(requireActivity(), ThreadActivity::class.java)
        startActivity(intent)
    }
}
