package com.vicegym.qrtrainertruckadminapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.vicegym.qrtrainertruckadminapp.AddNewTrainingActivity
import com.vicegym.qrtrainertruckadminapp.adapter.TrainingsAdapter
import com.vicegym.qrtrainertruckadminapp.databinding.FragmentTrainingsBinding

class TrainingsFragment : Fragment() {
    private lateinit var binding: FragmentTrainingsBinding
    private lateinit var trainingsAdapter: TrainingsAdapter

    companion object {
        fun newInstance() =
            TrainingsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrainingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        trainingsAdapter = TrainingsAdapter(requireContext())
        binding.rvTrainings.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTrainings.adapter = trainingsAdapter
        initTrainingsListener()
        binding.btnAddTraining.setOnClickListener { startActivity(Intent(requireContext(), AddNewTrainingActivity::class.java)) }
    }

    private fun initTrainingsListener() {
        val db = Firebase.firestore
        db.collection("trainings")
            .orderBy("date") //TODO: ha date vhogy bugos akkor sorter használata
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> trainingsAdapter.addTrainings(dc.document.toObject())
                        DocumentChange.Type.MODIFIED -> Toast.makeText(
                            requireContext(),
                            dc.document.data.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        DocumentChange.Type.REMOVED -> Toast.makeText(
                            requireContext(),
                            dc.document.data.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }
}