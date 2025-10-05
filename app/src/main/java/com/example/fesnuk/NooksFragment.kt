package com.example.fesnuk

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NooksFragment : Fragment(), NookAdapter.OnExploreButtonClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nooks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.nooksRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val nooks = listOf(
            Nook("::Space", "All about space", 1500, R.drawable.nook_image_view),
            Nook("::Gaming", "All about gaming", 2500, R.drawable.nook_image_view),
            Nook("::Technology", "All about technology", 1800, R.drawable.nook_image_view)
        )

        val adapter = NookAdapter(nooks, this)
        recyclerView.adapter = adapter
    }

    override fun onExploreClick(nook: Nook) {
        val intent = Intent(requireActivity(), NooksActivity::class.java)
        startActivity(intent)
    }
}
