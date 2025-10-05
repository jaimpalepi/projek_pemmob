package com.example.fesnuk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    // Buat instance dari fragment Anda
    private val homeFragment = HomeFragment()
    private val nooksFragment = NooksFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tabLayout: TabLayout = findViewById(R.id.tabLayout)

        // Tampilkan fragment awal (Home) saat aplikasi pertama kali dibuka
        if (savedInstanceState == null) {
            replaceFragment(homeFragment)
        }

        // Atur listener untuk saat tab dipilih
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> replaceFragment(homeFragment) // Posisi 0 adalah "Home"
                    1 -> replaceFragment(nooksFragment) // Posisi 1 adalah "Nooks"
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Tidak perlu melakukan apa-apa di sini
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Tidak perlu melakukan apa-apa di sini
            }
        })
    }

    // Fungsi untuk mengganti fragment di dalam container
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            commit()
        }
    }
}
    