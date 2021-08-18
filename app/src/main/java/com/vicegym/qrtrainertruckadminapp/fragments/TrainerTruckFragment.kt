package com.vicegym.qrtrainertruckadminapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vicegym.qrtrainertruckadminapp.databinding.FragmentTrainerTruckBinding

class TrainerTruckFragment : Fragment() {

    private lateinit var binding: FragmentTrainerTruckBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrainerTruckBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TrainerTruckFragment()
    }
}