package com.vicegym.qrtrainertruckadminapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vicegym.qrtrainertruckadminapp.databinding.ActivityMainBinding
import com.vicegym.qrtrainertruckadminapp.fragments.ForumFragment
import com.vicegym.qrtrainertruckadminapp.fragments.TrainerTruckFragment
import com.vicegym.qrtrainertruckadminapp.fragments.TrainingsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val trainingsFragment = TrainingsFragment.newInstance()
        val trainerTruckFragment = TrainerTruckFragment.newInstance()
        val forumFragment = ForumFragment.newInstance()

        supportFragmentManager.beginTransaction().apply { replace(R.id.container, trainingsFragment).commit() }

        binding.bottomNavBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_trainings -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.container, trainingsFragment).commit()
                    }
                    true
                }

                R.id.menu_location -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.container, trainerTruckFragment).commit()
                    }
                    true
                }

                R.id.menu_forum -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.container, forumFragment).commit()
                    }
                    true
                }

                else -> false
            }
        }
    }
}